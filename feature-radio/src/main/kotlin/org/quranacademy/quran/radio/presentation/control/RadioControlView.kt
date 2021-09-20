package org.quranacademy.quran.radio.presentation.control

import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import org.quranacademy.quran.presentation.mvp.BaseMvpView

@StateStrategyType(AddToEndSingleStrategy::class)
interface RadioControlView : BaseMvpView {

    fun showRadioControl(isVisible: Boolean)

    fun changeRadioState(isPlaying: Boolean)

}