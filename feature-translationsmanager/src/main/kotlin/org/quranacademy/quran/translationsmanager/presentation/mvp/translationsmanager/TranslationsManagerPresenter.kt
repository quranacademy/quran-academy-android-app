package org.quranacademy.quran.translationsmanager.presentation.mvp.translationsmanager

import com.arellomobile.mvp.InjectViewState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.quranacademy.quran.core.ui.R
import org.quranacademy.quran.data.downloading.DownloadedCancelledException
import org.quranacademy.quran.di.PrimitiveWrapper
import org.quranacademy.quran.domain.commons.ResourcesManager
import org.quranacademy.quran.domain.exceptions.NoNetworkException
import org.quranacademy.quran.domain.exceptions.NoSpaceLeftException
import org.quranacademy.quran.domain.models.FileDownloadInfo
import org.quranacademy.quran.presentation.extensions.replace
import org.quranacademy.quran.presentation.mvp.BasePresenter
import org.quranacademy.quran.presentation.mvp.ErrorHandler
import org.quranacademy.quran.translationsmanager.di.IsInitialSetupMode
import org.quranacademy.quran.translationsmanager.di.IsTafseers
import org.quranacademy.quran.translationsmanager.domain.translations.TranslationUIModel
import org.quranacademy.quran.translationsmanager.domain.translations.TranslationsInteractor
import javax.inject.Inject

@InjectViewState
class TranslationsManagerPresenter @Inject constructor(
        @IsTafseers private val isTafseer: PrimitiveWrapper<Boolean>,
        @IsInitialSetupMode private val isInitialSetupMode: PrimitiveWrapper<Boolean>,
        private val interactor: TranslationsInteractor
) : BasePresenter<TranslationsManagerView>() {

    private lateinit var translations: MutableList<TranslationUIModel>

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

    fun onDownloadTranslationClicked(translation: TranslationUIModel) {
        downloadTranslation(translation, false)
    }

    fun onUpdateTranslationClicked(translation: TranslationUIModel) {
        downloadTranslation(translation, true)
    }

    fun onCancelDownloadingClicked(translation: TranslationUIModel) = launch {
        interactor.cancelTranslationDownloading(translation)
        updateTranslation(translation.code, translation.copy(isDownloading = false))
    }

    fun onDeleteTranslationClicked(translation: TranslationUIModel) {
        viewState.showDeleteTranslationDialog(translation)
    }

    fun deleteTranslation(translation: TranslationUIModel) = launch {
        interactor.deleteTranslation(translation)
        val translationCopy = translation.copy(isDownloaded = false, isEnabled = false)
        translations.replace(translation, translationCopy)
        showTranslations(true)
    }

    fun onEnableTranslationClicked(translation: TranslationUIModel, isEnabled: Boolean) = launch {
        val translationCopy = translation.copy(isEnabled = isEnabled)
        translations.replace(translation, translationCopy)
        showTranslations(true)
        interactor.enableTranslation(translation, isEnabled)

    }

    private fun loadTranslationsList(forceLoad: Boolean = false) = launch {
        try {
            viewState.updateTranslationsListVisibility(false)
            viewState.showProgressLayout(true)
            translations = interactor.getTranslations(forceLoad, isTafseer.value).toMutableList()
            viewState.updateTranslationsListVisibility(true)

            showTranslations()
            showDownloadingTranslationsProgress()
        } catch (error: Throwable) {
            onLoadTranslationsListError(error)
        } finally {
            viewState.showProgressLayout(false)
        }
    }

    private fun showDownloadingTranslationsProgress() {
        interactor.setCurrentTranslationsDownloadingListener { code, progress ->
            val translation = translations.firstOrNull { it.code == code }
            if (translation != null) {
                if (progress.totalSize != FileDownloadInfo.IS_FINISHED) {
                    if (!translation.isDownloading) {
                        val newItem = translation.copy(isDownloading = false)
                        translations.replace(translation, newItem)
                    }
                    viewState.updateTranslationDownloadProgress(translation, progress)
                } else {
                    onDownloadTranslationSuccess(translation, translation.isDownloaded)
                }
            }
        }
    }

    private fun showTranslations(update: Boolean = false) {
        if (update) {
            viewState.updateTranslations(translations, isInitialSetupMode.value)
        } else {
            viewState.showTranslations(translations, isInitialSetupMode.value)
        }
    }

    private fun downloadTranslation(
            translation: TranslationUIModel,
            isUpdate: Boolean
    ) = GlobalScope.launch(context = Dispatchers.Main) {
        try {
            translations.replace(translation, translation.copy(isDownloading = true))
            showTranslations(true)
            if (isUpdate) {
                interactor.downloadTranslationUpdate(translation) {
                    viewState.updateTranslationDownloadProgress(translation, it)
                }
            } else {
                interactor.downloadTranslation(translation) {
                    viewState.updateTranslationDownloadProgress(translation, it)
                }
            }
            onDownloadTranslationSuccess(translation, isUpdate)
        } catch (error: DownloadedCancelledException) {
            translations.replace(translation, translation.copy(isDownloading = false))
        } catch (error: Exception) {
            onDownloadTranslationError(error)
            updateTranslation(translation.code, translation.copy(isDownloading = false))
        } finally {
            viewState.updateTranslationDownloadProgress(translation, null)
            showTranslations(true)
        }
    }

    private fun onDownloadTranslationSuccess(translation: TranslationUIModel, isUpdating: Boolean) {
        val isEnabled = if (isUpdating) translation.isEnabled else true
        val newTranslation = translation.copy(
                isDownloading = false,
                isDownloaded = true,
                isUpdateAvailable = false,
                isEnabled = isEnabled
        )
        updateTranslation(newTranslation.code, newTranslation)
    }

    private fun onLoadTranslationsListError(error: Throwable) {
        when (error) {
            is NoNetworkException -> viewState.showNoNetworkLayout(true)
            else -> errorHandler.proceed(error) { viewState.showMessage(it) }
        }
    }

    private fun onDownloadTranslationError(error: Throwable) {
        when (error) {
            is NoSpaceLeftException -> viewState.showMessage(resourcesManager.getString(R.string.not_enough_free_space_on_disk_message))
            else -> errorHandler.proceed(error, viewState::showMessage)
        }
    }

    private fun updateTranslation(code: String, newItem: TranslationUIModel) {
        translations.firstOrNull { it.code == code }
                ?.let { translations.replace(it, newItem) }
        showTranslations(true)
    }

}