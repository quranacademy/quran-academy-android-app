package org.quranacademy.quran.radio.presentation.start

import com.arellomobile.mvp.InjectViewState
import org.quranacademy.quran.RadioPlayerSynchronizer
import org.quranacademy.quran.presentation.mvp.BasePresenter
import org.quranacademy.quran.radio.domain.RadioControl
import javax.inject.Inject

@InjectViewState
class RadioPresenter @Inject constructor(
        private val radioControl: RadioControl,
        private val radioPlayerSynchronizer: RadioPlayerSynchronizer
) : BasePresenter<RadioView>() {

    fun onStartRadioClicked() {
        if (radioPlayerSynchronizer.isPlayerActive()) {
            radioPlayerSynchronizer.stopPlayer {
                radioControl.playRadio()
            }
        } else {
            radioControl.playRadio()
        }
    }

}