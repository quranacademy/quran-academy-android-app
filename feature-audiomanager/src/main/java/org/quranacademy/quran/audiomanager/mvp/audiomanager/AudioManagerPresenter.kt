package org.quranacademy.quran.audiomanager.mvp.audiomanager

import com.arellomobile.mvp.InjectViewState
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.quranacademy.quran.audiomanager.domain.AudioManagerInteractor
import org.quranacademy.quran.core.ui.R
import org.quranacademy.quran.domain.exceptions.NoNetworkException
import org.quranacademy.quran.domain.exceptions.NoSpaceLeftException
import org.quranacademy.quran.domain.models.Recitation
import org.quranacademy.quran.presentation.mvp.BasePresenter
import org.quranacademy.quran.presentation.mvp.routing.screens.RecitationInfoScreen
import org.quranacademy.quran.recitationsrepository.downloading.AudioDownloadException
import javax.inject.Inject

@InjectViewState
class AudioManagerPresenter @Inject constructor(
        private val interactor: AudioManagerInteractor
) : BasePresenter<AudioManagerView>() {

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()

        GlobalScope.launch {
            interactor.getAudioFilesUpdates()
                    .collect { loadRecitationsList() }
        }

        loadRecitationsList()
    }

    fun onRetryLoadRecitationsListClicked() {
        viewState.showNoNetworkLayout(false)
        loadRecitationsList(true)
    }

    fun refreshRecitationsList() {
        loadRecitationsList(true)
    }

    fun onDownloadRecitationAudioClicked(recitation: Recitation) = launch {
        try {
            viewState.showRecitationAudioDownloadingProgress(recitation)
            interactor.downloadRecitationAudio(recitation) {
                viewState.updateRecitationDownloadProgress(it)
            }
            viewState.hideRecitationDownloadProgress()
            showTranslatRecitations(true)
        } catch (error: Exception) {
            val errorMessage = when (error) {
                is NoSpaceLeftException -> resourcesManager.getString(R.string.not_enough_free_space_on_disk_message)
                is AudioDownloadException.NoNetwork -> resourcesManager.getString(R.string.audio_download_network_error_message)
                else -> errorHandler.getMessageForError(error)
            }
            viewState.showMessage(errorMessage)
        } finally {
            viewState.hideRecitationDownloadProgress()
        }
    }

    fun onCancelRecitationAudioDownloadingClicked() = launch {
        interactor.cancelRecitationDownloading()
    }

    fun onDeleteRecitationAudioClicked(recitation: Recitation) = launch {
        interactor.deleteRecitationAudio(recitation)
    }

    fun onOpenRecitationDetailsClicked(recitation: Recitation) {
        router.goForward(RecitationInfoScreen(recitation.id))
    }

    private fun loadRecitationsList(loadFromInternet: Boolean = false) = launch {
        try {
            viewState.updateRecitationsListVisibility(false)
            viewState.showProgressLayout(true)
            val recitationsAudioInfo = interactor.getRecitationsInfoList(loadFromInternet)
            viewState.updateRecitationsListVisibility(true)
            viewState.showRecitations(recitationsAudioInfo)
        } catch (error: Exception) {
            onLoadTranslationsListError(error)
        } finally {
            viewState.showProgressLayout(false)
        }
    }

    private fun showTranslatRecitations(update: Boolean = false) {
        if (update) {
            //viewState.updateRecitations(recitations)
        } else {
            // viewState.showRecitations(recitations)
        }
    }

    private fun onLoadTranslationsListError(error: Throwable) {
        when (error) {
            is NoNetworkException -> viewState.showNoNetworkLayout(true)
            else -> errorHandler.proceed(error) { viewState.showMessage(it) }
        }
    }

}