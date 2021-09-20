package org.quranacademy.quran.mushaf.presentation.mvp.ayahtranslation

import com.arellomobile.mvp.InjectViewState
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.quranacademy.quran.domain.models.AyahDetails
import org.quranacademy.quran.domain.models.AyahId
import org.quranacademy.quran.mushaf.di.ayahtranslations.AyahIds
import org.quranacademy.quran.mushaf.domain.ayahtranslation.AyahTranslationsInteractor
import org.quranacademy.quran.mushaf.domain.ayahtranslation.EnabledTranslationsNotFound
import org.quranacademy.quran.presentation.mvp.BasePresenter
import org.quranacademy.quran.presentation.mvp.routing.screens.SettingsScreen
import javax.inject.Inject

@InjectViewState
class AyahTranslationPresenter @Inject constructor(
        @AyahIds private val ayahs: List<AyahId>,
        private val interactor: AyahTranslationsInteractor
) : BasePresenter<AyahTranslationView>() {

    private var currentAyahPosition = 0
    private lateinit var ayahTranslations: List<AyahDetails>

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()

        launch {
            interactor.getEnabledTranslationsListChangedUpdates().collect {
                loadAyahTranslations()
            }
        }

        loadAyahTranslations()
    }

    fun onOpenTranslationsScreenClicked() {
        router.goForward(SettingsScreen())
    }

    fun onPrevAyahButtonClicked() {
        if (!isTranslationsLoaded() || isFirstAyah()) {
            return
        }

        currentAyahPosition -= 1
        viewState.switchToTranslation(currentAyahPosition)
    }

    fun onNextAyahButtonClicked() {
        if (!isTranslationsLoaded() || isLastAyah()) {
            return
        }

        currentAyahPosition += 1
        viewState.switchToTranslation(currentAyahPosition)
    }

    fun onAyahChanged(position: Int) {
        currentAyahPosition = position
        updateNavigationButtonsVisibility()
    }

    private fun isTranslationsLoaded() = ::ayahTranslations.isInitialized

    private fun loadAyahTranslations() = launch {
        try {
            viewState.showProgressLayout(true)
            val translations = interactor.getAyahTranslations(ayahs)
            oAyahTranslationsLoaded(translations)
        } catch (error: Exception) {
            onAyahTranslationsLoadingError(error)
        } finally {
            viewState.showProgressLayout(false)
        }
    }

    private fun oAyahTranslationsLoaded(ayahTranslations: List<AyahDetails>) {
        this.ayahTranslations = ayahTranslations
        viewState.showEnabledTranslationsNotFound(false)
        viewState.updateAyahTranslationsVisibility(true)
        viewState.showAyahTranslations(ayahTranslations)

        updateNavigationButtonsVisibility()
    }

    private fun onAyahTranslationsLoadingError(error: Throwable) {
        when (error) {
            is EnabledTranslationsNotFound -> {
                viewState.showEnabledTranslationsNotFound(true)
                viewState.updateAyahTranslationsVisibility(false)
            }
            else -> errorHandler.proceed(error) { viewState.showMessage(it) }
        }
    }

    private fun updateNavigationButtonsVisibility() {
        val isMultipleAyahsSelected = ayahTranslations.size > 1
        if (isMultipleAyahsSelected) {
            val showPrevButton = !isFirstAyah()
            val showNextButton = !isLastAyah()
            viewState.showNavigationButtons(showPrevButton, showNextButton)
        } else {
            viewState.showNavigationButtons(false, false)
        }
    }

    private fun isFirstAyah() = currentAyahPosition == 0

    private fun isLastAyah() = currentAyahPosition == ayahTranslations.size - 1

}