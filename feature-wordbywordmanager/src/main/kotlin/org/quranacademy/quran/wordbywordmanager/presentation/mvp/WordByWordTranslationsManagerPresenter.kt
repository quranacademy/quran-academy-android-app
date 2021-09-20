package org.quranacademy.quran.wordbywordmanager.presentation.mvp

import com.arellomobile.mvp.InjectViewState
import kotlinx.coroutines.launch
import org.quranacademy.quran.di.PrimitiveWrapper
import org.quranacademy.quran.domain.commons.ResourcesManager
import org.quranacademy.quran.domain.exceptions.NoNetworkException
import org.quranacademy.quran.domain.exceptions.NoSpaceLeftException
import org.quranacademy.quran.presentation.extensions.replace
import org.quranacademy.quran.presentation.mvp.BasePresenter
import org.quranacademy.quran.presentation.mvp.ErrorHandler
import org.quranacademy.quran.wordbywordmanager.R
import org.quranacademy.quran.wordbywordmanager.di.IsInitialSetupMode
import org.quranacademy.quran.wordbywordmanager.domain.WordByWordTranslationUIModel
import org.quranacademy.quran.wordbywordmanager.domain.WordByWordTranslationsInteractor
import javax.inject.Inject

@InjectViewState
class WordByWordTranslationsManagerPresenter @Inject constructor(
        @IsInitialSetupMode private val isInitialSetupMode: PrimitiveWrapper<Boolean>,
        private val interactor: WordByWordTranslationsInteractor
) : BasePresenter<WordByWordTranslationsManagerView>() {

    private lateinit var translations: MutableList<WordByWordTranslationUIModel>
    private var currentTranslation: WordByWordTranslationUIModel? = null

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()

        loadTranslationsList()
    }

    fun onRetryTranslationsListLoadingClicked() {
        viewState.showNoNetworkLayout(false)
        loadTranslationsList()
    }

    fun refreshTranslationsList() {
        loadTranslationsList(true)
    }

    fun onDownloadTranslationClicked(translation: WordByWordTranslationUIModel) = launch {
        try {
            viewState.showTranslationDownloadingProgress(translation)
            interactor.downloadTranslation(translation) {
                viewState.updateTranslationDownloadProgress(it)
            }
            onDownloadTranslationSuccess(translation)
        } catch (error: Exception) {
            onDownloadTranslationError(error)
        } finally {
            viewState.hideTranslationDownloadProgress()
        }

    }

    fun onUpdateTranslationClicked(translation: WordByWordTranslationUIModel) = launch {
        try {
            viewState.showTranslationDownloadingProgress(translation)
            interactor.downloadTranslation(translation) {
                viewState.updateTranslationDownloadProgress(it)
            }
            onTranslationUpdatingSuccess(translation)
        } catch (error: Exception) {
            onDownloadTranslationError(error)
        } finally {
            viewState.hideTranslationDownloadProgress()
        }
    }

    fun onCancelDownloadingClicked(translation: WordByWordTranslationUIModel) = launch {
        interactor.cancelTranslationDownloading(translation)
    }

    fun onDeleteTranslationClicked(translation: WordByWordTranslationUIModel) {
        viewState.showDeleteTranslationDialog(translation)
    }

    fun deleteTranslation(translation: WordByWordTranslationUIModel) = launch {
        interactor.deleteTranslation(translation)
        val translationCopy = translation.copy(isDownloaded = false, isEnabled = false)
        replaceTranslation(translation, translationCopy)
        showTranslations(true)
    }

    fun onEnableTranslationClicked(translation: WordByWordTranslationUIModel) = launch {
        interactor.enableTranslation(translation)
        setCurrentTranslation(translation)
        showTranslations(true)
    }

    private fun loadTranslationsList(forceLoadFromServer: Boolean = false) = launch {
        try {
            viewState.updateTranslationsListVisibility(false)
            viewState.showProgressLayout(true)
            val translationsWrapper = interactor.getTranslations(forceLoadFromServer)
            viewState.updateTranslationsListVisibility(true)
            translations = translationsWrapper.translations.toMutableList()
            currentTranslation = translationsWrapper.currentTranslation
            showTranslations()
        } catch (error: Exception) {
            onLoadTranslationsListError(error)
        } finally {
            viewState.showProgressLayout(false)
        }
    }

    private fun showTranslations(update: Boolean = false) {
        if (update) {
            viewState.updateTranslations(translations, isInitialSetupMode.value)
        } else {
            viewState.showTranslations(translations, isInitialSetupMode.value)
        }
    }

    private fun onDownloadTranslationSuccess(translation: WordByWordTranslationUIModel) {
        val translationCopy = translation.copy(isDownloaded = true, isEnabled = true)
        replaceTranslation(translation, translationCopy)
        setCurrentTranslation(translationCopy)
        showTranslations(true)
    }

    private fun onTranslationUpdatingSuccess(translation: WordByWordTranslationUIModel) {
        //убираем флаг, чтобы пользователь повторно не видел кнопку для обновления
        val translationCopy = translation.copy(isUpdateAvailable = false)
        translations.replace(translation, translationCopy)
        showTranslations(true)
    }

    private fun onLoadTranslationsListError(error: Throwable) {
        when (error) {
            is NoNetworkException -> viewState.showNoNetworkLayout(true)
            else -> errorHandler.proceed(error) { viewState.showMessage(it) }
        }
    }

    private fun onDownloadTranslationError(error: Throwable) {
        when (error) {
            is NoSpaceLeftException -> viewState.showMessage(resources.getString(R.string.not_enough_free_space_on_disk_message))
            else -> errorHandler.proceed(error) { viewState.showMessage(it) }
        }
    }

    private fun setCurrentTranslation(translation: WordByWordTranslationUIModel) {
        val currentTranslation = this.currentTranslation
        if (currentTranslation != null) {
            val currentTranslationCopy = currentTranslation.copy(isEnabled = false)
            replaceTranslation(currentTranslation, currentTranslationCopy)
        }
        val translationCopy = translation.copy(isEnabled = true)
        replaceTranslation(translation, translationCopy)
        this.currentTranslation = translationCopy
    }

    private fun replaceTranslation(old: WordByWordTranslationUIModel, new: WordByWordTranslationUIModel) {
        translations.replace(old, new) { o1, o2 -> o1.id == o2.id }
    }

}