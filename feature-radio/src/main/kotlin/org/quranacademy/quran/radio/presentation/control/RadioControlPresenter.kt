package org.quranacademy.quran.radio.presentation.control

import com.arellomobile.mvp.InjectViewState
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.quranacademy.quran.domain.commons.ResourcesManager
import org.quranacademy.quran.presentation.mvp.BasePresenter
import org.quranacademy.quran.radio.R
import org.quranacademy.quran.radio.data.RadioException
import org.quranacademy.quran.radio.data.manager.RadioData
import org.quranacademy.quran.radio.data.radio.RadioState
import org.quranacademy.quran.radio.domain.RadioControl
import javax.inject.Inject

@InjectViewState
class RadioControlPresenter @Inject constructor(
        private val radioData: RadioData,
        private val radioControl: RadioControl
) : BasePresenter<RadioControlView>() {

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()

        updateViewState(radioData.playbackState)
        launch {
            radioData.radioStateUpdates().collect {
                updateViewState(it)
            }
        }

        launch {
            radioData.radioErrorUpdates().collect {
                val messageResId = if (it is RadioException.Network) {
                    R.string.network_error
                } else {
                    R.string.unknown_error
                }
                viewState.showMessage(resourcesManager.getString(messageResId))
            }
        }
    }

    fun onPlayPauseButtonClicked() {
        if (radioData.playbackState == RadioState.PLAYING) {
            radioControl.pauseRadio()
        } else {
            radioControl.playRadio()
        }
    }


    fun onStopRadioClicked() {
        radioControl.stopRadio()
    }

    private fun updateViewState(state: RadioState) {
        val isActive = state == RadioState.PLAYING
                || state == RadioState.PAUSED
                || state == RadioState.PREPARING
        viewState.showRadioControl(isActive)

        val isPlaying = state == RadioState.PLAYING
        viewState.changeRadioState(isPlaying)
    }

}