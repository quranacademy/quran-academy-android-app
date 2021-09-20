package org.quranacademy.quran.mainscreen.presentation.mvp.surahslist

import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy
import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import org.quranacademy.quran.presentation.mvp.BaseMvpView

@StateStrategyType(AddToEndSingleStrategy::class)
interface SurahsListView : BaseMvpView {

    fun showProgressLayout(isVisible: Boolean)

    fun updateSurahsVisibility(isVisible: Boolean)

    fun showSurahs(surahsGroupedByJuz: List<QuranJuz>)

    fun scrollTo(position: Int)

}