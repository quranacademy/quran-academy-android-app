package org.quranacademy.quran.mainscreen.presentation.mvp

import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy
import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import org.quranacademy.quran.presentation.mvp.BaseMvpView

@StateStrategyType(AddToEndSingleStrategy::class)
interface MainView : BaseMvpView {

    @StateStrategyType(OneExecutionStateStrategy::class)
    fun showTranslationsCopyrightDialog()

    @StateStrategyType(OneExecutionStateStrategy::class)
    fun showTranslationsUpdatesAvailable(isVisible: Boolean)

    fun showPlayerButton(isShowing: Boolean)

    fun showPlayerControlPanel()

    fun hidePlayerControlPanel()

}