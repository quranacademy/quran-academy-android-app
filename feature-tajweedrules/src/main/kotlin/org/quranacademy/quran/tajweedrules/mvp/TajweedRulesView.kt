package org.quranacademy.quran.tajweedrules.mvp

import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import org.quranacademy.quran.presentation.mvp.BaseMvpView

@StateStrategyType(AddToEndSingleStrategy::class)
interface TajweedRulesView : BaseMvpView {

    fun showStoppingSigns(stoppingSigns: List<StoppingSign>)


    fun showTajweedRules(tajweedRules: List<TajweedRule>)

    fun setCurrentPlayingAudio(tajweedRule: TajweedRule?)

}
