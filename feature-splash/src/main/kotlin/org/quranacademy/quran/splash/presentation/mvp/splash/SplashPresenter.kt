package org.quranacademy.quran.splash.presentation.mvp.splash

import com.arellomobile.mvp.InjectViewState
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import me.aartikov.alligator.Screen
import org.quranacademy.quran.appmigration.AppMigrationManager
import org.quranacademy.quran.domain.exceptions.NoNetworkException
import org.quranacademy.quran.domain.models.Language
import org.quranacademy.quran.presentation.mvp.BasePresenter
import org.quranacademy.quran.presentation.mvp.routing.screens.*
import org.quranacademy.quran.splash.R
import org.quranacademy.quran.splash.domain.splash.SplashInteractor
import javax.inject.Inject

@InjectViewState
class SplashPresenter @Inject constructor(
        private val interactor: SplashInteractor,
        private val appMigrationManager: AppMigrationManager
) : BasePresenter<SplashView>() {

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()

        launch {
            if (appMigrationManager.isMigrationNeeded()) {
                viewState.showPreparingProgress(true)
                viewState.showMessage(resourcesManager.getString(R.string.data_migration_message))
                appMigrationManager.subscribeOnMigrationFinished()
                        .collect { isMigrationFinished ->
                            if (isMigrationFinished) {
                                viewState.showPreparingProgress(false)
                                viewState.hideStatusMessage()
                                checkIsUpdateNeeded()
                            }
                        }
            } else {
                checkIsUpdateNeeded()
            }
        }
    }

    fun onRetryLoadLanguagesListClicked() {
        //Нельзя начинать установку, не проверив устарела версия или нет
        checkIsUpdateNeeded()
    }

    fun onExitButtonClicked() {
        router.finish()
    }

    fun onLanguageSelected(language: Language) = launch {
        try {
            viewState.showDownloadProgress(true)
            interactor.downloadData(language) {
                viewState.showDownloadProgress(it)
            }
            viewState.showDownloadProgress(false)
            router.goForward(IntroScreen())
        } catch (error: Exception) {
            errorHandler.proceed(error, viewState::showMessage)
        }
    }

    private fun checkIsUpdateNeeded() {
        launch {
            try {
                val isAppUpdateNeeded = interactor.isAppUpdateNeeded()
                if (isAppUpdateNeeded) {
                    router.reset(UpdateNeededScreen())
                }
            } catch (error: Exception) {

            }
        }

        checkIsInitialSetupCompleted()
    }

    private fun checkIsInitialSetupCompleted() = launch {
        val isInitialSetupCompleted = interactor.isInitialSetupCompleted()
        if (isInitialSetupCompleted) {
            openNextScreen()
        } else {
            startInitialSetup()
        }
    }

    private fun startInitialSetup() = launch {
        try {
            val languages = interactor.getLanguages()
            viewState.showLanguagesDialog(languages)
        } catch (error: Exception) {
            when (error) {
                is NoNetworkException -> viewState.showCheckNetworkDialog()
                else -> errorHandler.proceed(error, viewState::showMessage)
            }
        }
    }

    private fun openNextScreen() = launch {
        val readSettings = interactor.getReadSettings()
        if (readSettings.isOpenLastReadingPlaceEnabled) {
            val readingScreen: Screen = if (readSettings.isMushafMode) {
                MushafScreen()
            } else {
                SurahDetailsScreen()
            }
            router.reset(MainScreen())
            router.goForward(readingScreen)

        } else {
            router.reset(MainScreen())
        }
    }

}