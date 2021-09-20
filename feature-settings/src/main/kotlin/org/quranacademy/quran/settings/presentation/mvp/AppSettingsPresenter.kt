package org.quranacademy.quran.settings.presentation.mvp

import com.arellomobile.mvp.InjectViewState
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.quranacademy.quran.core.ui.R
import org.quranacademy.quran.data.downloading.DownloadException
import org.quranacademy.quran.presentation.mvp.BasePresenter
import org.quranacademy.quran.settings.domain.AppSettingsInteractor
import org.quranacademy.quran.settings.domain.StoragesInfo
import org.quranacademy.quran.settings.domain.StoragesInfo.Storage
import javax.inject.Inject

@InjectViewState
class AppSettingsPresenter @Inject constructor(
        private val interactor: AppSettingsInteractor
) : BasePresenter<AppSettingsView>() {

    private var imagesBundleDownloadJob: Job? = null
    private lateinit var storagesInfo: StoragesInfo

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()

        launch {
            interactor.getTranslationUpdatesCountListener(false)
                    .collect { viewState.showTranslationUpdatesCount(it) }
        }

        launch {
            interactor.getTranslationUpdatesCountListener(true)
                    .collect { viewState.showTafseerUpdatesCount(it) }
        }

        launch {
            interactor.getWordByWordTranslationUpdatesCountListener()
                    .collect { viewState.showWordByWordTranslationUpdatesCount(it) }
        }

        launch {
            val translationUpdatesCount = interactor.getTranslationUpdatesCount(false)
            viewState.showTranslationUpdatesCount(translationUpdatesCount)

            val tafseerUpdatesCount = interactor.getTranslationUpdatesCount(true)
            viewState.showTafseerUpdatesCount(tafseerUpdatesCount)

            val wbwTranslationUpdatesCount = interactor.getWordByWordTranslationUpdatesCount()
            viewState.showWordByWordTranslationUpdatesCount(wbwTranslationUpdatesCount)

            val historySize = interactor.getReadingHistorySize()
            viewState.showReadingHistorySize(historySize)

            this@AppSettingsPresenter.storagesInfo = interactor.getStoragesList()
            if (storagesInfo.isExternalStorageMounted()) {
                viewState.showStorageChoosingPref()
            }

            val mushafPageType = interactor.getMushafPageType()
            viewState.showMushafPagesType(mushafPageType)
            val isAllImagesDownloaded = interactor.isAllImagesDownloaded()
            if (isAllImagesDownloaded) {
                viewState.hideImagesBundleDownloadingItem()
            }
        }
    }

    fun onSelectReadingHistorySizeClicked() {
        viewState.showReadingHistorySizeSelectionDialog()
    }

    fun onReadingHistorySizeSelected(size: Int) {
        viewState.showReadingHistorySize(size)
        interactor.setReadingHistorySize(size)
    }

    fun onDownloadAllMushafImagesClicked() {
        imagesBundleDownloadJob = launch {
            try {
                viewState.showImagesBundleDownloadProgress(true)
                interactor.downloadImagesBundle {
                    viewState.updateImagesBundleDownloadProgress(it)
                }
                viewState.showMessage(resourcesManager.getString(R.string.images_bundle_downloading_finished_message))
            } catch (error: DownloadException) {
                errorHandler.proceed(error.cause!!, viewState::showMessage)
            } catch (error: Exception) {
                if (error !is CancellationException) {
                    errorHandler.proceed(error, viewState::showMessage)
                }
            } finally {
                viewState.showImagesBundleDownloadProgress(false)
            }
        }
    }

    fun onCancelImagesBundleDownloadingClicked() = launch {
        viewState.showImagesBundleDownloadProgress(false)
        imagesBundleDownloadJob?.cancel()
        interactor.cancelImagesBundleDownloading()

    }

    fun onChooseFilesLocationClicked() {
        val storages = interactor.getStoragesList()
        viewState.showStorageChoosingDialog(storages)
    }

    fun onStorageSelected(storage: Storage) = launch {
        if (storage.folderPath == storagesInfo.currentStoragePath) {
            return@launch
        }

        if (interactor.isTranslationsDownloading()) {
            viewState.showMessage(resourcesManager.getString(R.string.wait_translations_downloading_message))
            return@launch
        }

        if (storagesInfo.usedAppSpace >= storage.freeSpace) {
            viewState.showMessage(resourcesManager.getString(R.string.no_enough_space_to_move_files))
            return@launch
        }

        try {
            viewState.showAppDataTransferProgress(true)
            interactor.setCurrentAppStorage(storage)
            viewState.showMessage(resourcesManager.getString(R.string.copying_finished_message))
            storagesInfo = storagesInfo.copy(currentStoragePath = storage.folderPath)
        } catch (error: Exception) {
            errorHandler.proceed(error)
        } finally {
            viewState.showAppDataTransferProgress(false)
        }
    }

}