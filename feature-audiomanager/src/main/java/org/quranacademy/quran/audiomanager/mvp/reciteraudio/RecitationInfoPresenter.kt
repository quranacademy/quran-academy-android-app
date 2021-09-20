package org.quranacademy.quran.audiomanager.mvp.reciteraudio

import com.arellomobile.mvp.InjectViewState
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.quranacademy.quran.QuranConstants
import org.quranacademy.quran.audiomanager.R
import org.quranacademy.quran.audiomanager.di.RecitationId
import org.quranacademy.quran.audiomanager.domain.RecitationInfoInteractor
import org.quranacademy.quran.di.PrimitiveWrapper
import org.quranacademy.quran.domain.commons.ResourcesManager
import org.quranacademy.quran.domain.models.FileDownloadInfo
import org.quranacademy.quran.presentation.mvp.BasePresenter
import org.quranacademy.quran.presentation.mvp.ErrorHandler
import org.quranacademy.quran.recitationsrepository.downloading.AudioDownloadException
import org.quranacademy.quran.recitationsrepository.recitationaudioinfo.SurahAudioInfo
import javax.inject.Inject

@InjectViewState
class RecitationInfoPresenter @Inject constructor(
        @RecitationId private val recitationId: PrimitiveWrapper<Long>,
        private val interactor: RecitationInfoInteractor
) : BasePresenter<RecitationInfoView>() {

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()

        launch {
            interactor.getAudioFilesUpdates()
                    .collect { loadSurahsAudioInfo() }
        }

        loadSurahsAudioInfo()
    }

    fun onDownloadSurahClicked(surahAudioInfo: SurahAudioInfo) = launch {
        try {
            viewState.showSurahDownloadProgress(
                    surahName = surahAudioInfo.surahName
            )
            val surahAyahsCount = QuranConstants.SURAH_AYAHS_NUMBER[surahAudioInfo.surahNumber - 1]
            interactor.downloadRecitationSurahAudio(recitationId.value, surahAudioInfo.surahNumber) {
                viewState.updateSurahDownloadProgress(
                        surahName = surahAudioInfo.surahName,
                        ayahNumber = it.ayahNumber,
                        ayahsCount = surahAyahsCount,
                        progress = FileDownloadInfo(it.downloadedSizeBytes, it.totalSize)
                )
            }
        } catch (error: AudioDownloadException.NoNetwork) {
            viewState.showMessage(resourcesManager.getString(R.string.audio_download_network_error_message))
        } catch (error: Exception) {
            errorHandler.proceed(error, viewState::showMessage)
        } finally {
            viewState.hideSurahDownloadProgress()
            loadSurahsAudioInfo()
        }
    }

    fun onRemoveSurahClicked(surahAudioInfo: SurahAudioInfo) = launch {
        interactor.deleteRecitationSurahAudio(recitationId.value, surahAudioInfo.surahNumber)
    }

    fun onCancelDownloadingClicked() = launch {
        interactor.cancelSurahAudioDownloading()
    }

    private fun loadSurahsAudioInfo() {
        launch {
            val recitationInfo = interactor.getRecitationsInfo(recitationId.value)
            viewState.showRecitationInfo(recitationInfo)
        }
    }

}