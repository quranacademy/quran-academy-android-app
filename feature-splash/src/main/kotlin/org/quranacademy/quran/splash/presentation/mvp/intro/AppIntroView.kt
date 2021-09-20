package org.quranacademy.quran.splash.presentation.mvp.intro

import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import org.quranacademy.quran.presentation.mvp.BaseMvpView

@StateStrategyType(AddToEndSingleStrategy::class)
interface AppIntroView : BaseMvpView {

    fun goToSlide(position: Int)

    fun setNextButtontText(text: String)

}