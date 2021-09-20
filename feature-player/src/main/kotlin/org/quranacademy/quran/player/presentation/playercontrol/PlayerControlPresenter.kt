package org.quranacademy.quran.player.presentation.playercontrol

import com.arellomobile.mvp.InjectViewState
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.quranacademy.quran.domain.models.AyahAudio
import org.quranacademy.quran.domain.models.PlaybackOptions
import org.quranacademy.quran.domain.models.PlayerState
import org.quranacademy.quran.domain.models.PlayerState.*
import org.quranacademy.quran.player.R
import org.quranacademy.quran.player.data.quranplayer.PlaybackData
import org.quranacademy.quran.player.data.quranplayer.PlaybackException
import org.quranacademy.quran.player.domain.PlayerControl
import org.quranacademy.quran.player.presentation.playercontrol.dynamicsettings.PlayerDynamicSettingsScreen
import org.quranacademy.quran.presentation.mvp.BasePresenter
import org.quranacademy.quran.recitationsrepository.downloading.AudioDownloadException
import timber.log.Timber
import javax.inject.Inject

@InjectViewState
class PlayerControlPresenter @Inject constructor(
        private val playbackData: PlaybackData,
        private val playerControl: PlayerControl
) : BasePresenter<PlayerControlView>() {

    private lateinit var currentAyah: AyahAudio
    private var isProgressTracking = false
    private var pausedByDynamicOptions = false

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()

        launch {
            playbackData.currentAudioUpdates()
                    .collect { currentAyah ->
                        this@PlayerControlPresenter.currentAyah = currentAyah
                        viewState.updateCurrentAudioInfo(currentAyah)
                    }
        }

        launch {
            playbackData.playbackStateUpdates()
                    .collect { state ->
                        onStateChanged(state)
                    }
        }

        launch {
            playbackData.currentAudioProgressUpdates()
                    .collect { progress ->
                        if (playbackData.playbackState == PLAYING && !isProgressTracking) {
                            viewState.updateProgress(progress)
                        }
                    }
        }

        launch {
            playbackData.currentAudio?.let {
                viewState.updateProgress(playbackData.playbackProgress)
            }
        }

        launch {
            playbackData.playbackErrorUpdates()
                    .collect { error -> onPlaybackError(error) }
        }

    }

    fun onPlayPauseButtonClicked() {
        playerControl.playPause()
    }

    fun onProgressTracking(isTracking: Boolean) {
        this.isProgressTracking = isTracking
    }

    fun onProgressSelected(progress: Long) {
        playerControl.seekTo(progress)
    }

    fun onPrevAudioButtonClicked() {
        val state = playbackData.playbackState
        val isUserInTheStartOfAudio = playbackData.playbackProgress > 1000
        //check that playback of the current audio started (i. e. we have no errors and audio is downloaded)
        val isPlaybackStarted = state == PLAYING || state == PAUSED
        val isPlaybackFinished = playbackData.playbackState == IDLE

        //If user in the start of the audio (playback time less than
        //one second) (i. e. user clicked "prev" button twice), we go to the previous ayah.
        val goToTheStartOfAudio = isUserInTheStartOfAudio && isPlaybackStarted || isPlaybackFinished
        if (goToTheStartOfAudio) {
            playerControl.seekTo(0)
        } else {
            //Otherwise we go to the start of current audio
            playerControl.prevAyah()
        }
    }

    fun onNextButtonClicked() {
        playerControl.nextAyah()
    }

    fun onStopButtonClicked() {
        playerControl.stop()
    }

    fun onOpenPlaybackSettingsClicked() {
        if (playbackData.playbackState == PLAYING) {
            playerControl.pause()
            pausedByDynamicOptions = true
        }
        router.goForward(PlayerDynamicSettingsScreen())
    }

    fun onPlayerOptionsChanged(options: PlaybackOptions?) {
        if (options != null) {
            playerControl.changePlaybackOptions(options)
        } else {
            if (pausedByDynamicOptions) {
                playerControl.resume()
                pausedByDynamicOptions = false
            }
        }
    }

    private fun onStateChanged(state: PlayerState) {
        when (state) {
            PLAYING -> {
                viewState.updatePlayPauseButton(false)
                viewState.setPlayPauseButtonEnabled(true)
            }
            PAUSED -> {
                viewState.updatePlayPauseButton(true)
                viewState.setPlayPauseButtonEnabled(true)
            }
            DOWNLOADING -> {
                //статус загрузки аудио
                viewState.showCurrentAudioPlaybackStatus(resourcesManager.getString(R.string.ayah_is_download_message))
                viewState.setPlayPauseButtonEnabled(false)
                viewState.showCurrentAudioLoading(true)
            }
            LOADING -> {
                viewState.showCurrentAudioPlaybackStatus("")
                viewState.setPlayPauseButtonEnabled(false)
                viewState.showCurrentAudioLoading(false)
            }
            IDLE -> {
            }
            ERROR -> {
            }
        }
    }

    private fun onPlaybackError(error: PlaybackException) {
        when (error.cause) {
            is AudioDownloadException.NoNetwork -> {
                viewState.showCurrentAudioPlaybackStatus(resourcesManager.getString(R.string.audio_download_network_error_message))
            }
            is AudioDownloadException.IncorrectAudioUrl -> {
                viewState.showCurrentAudioPlaybackStatus(resourcesManager.getString(R.string.ayah_download_incorrect_url_error_message))
                viewState.showCurrentAudioLoading(false)
                viewState.disableControlButtons()
            }
            is PlaybackException.Unknown, is AudioDownloadException.Unknown -> {
                viewState.showCurrentAudioPlaybackStatus(resourcesManager.getString(R.string.unknown_playback_error_message))
                viewState.showCurrentAudioLoading(false)
                viewState.disableControlButtons()
            }
            else -> Timber.e(error)
        }
    }

}