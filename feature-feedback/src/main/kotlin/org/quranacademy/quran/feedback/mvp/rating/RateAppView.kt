package org.quranacademy.quran.feedback.mvp.rating

import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy
import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import org.quranacademy.quran.presentation.mvp.BaseMvpView

@StateStrategyType(AddToEndSingleStrategy::class)
interface RateAppView : BaseMvpView {

    @StateStrategyType(OneExecutionStateStrategy::class)
    fun showRateOnGooglePlayDialog()

}