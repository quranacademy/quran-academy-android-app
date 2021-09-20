package org.quranacademy.quran.memorization.mvp.options

import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy
import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import org.quranacademy.quran.domain.models.Recitation
import org.quranacademy.quran.domain.models.Surah
import org.quranacademy.quran.memorization.models.MemorizationMode
import org.quranacademy.quran.presentation.mvp.BaseMvpView

@StateStrategyType(AddToEndSingleStrategy::class)
interface MemorizationOptionsView : BaseMvpView {

    fun showInitProgressBar(isVisible: Boolean)

    @StateStrategyType(OneExecutionStateStrategy::class)
    fun showMemorizationModesSelect(modes: List<MemorizationMode>)

    fun updateMemorizationMode(mode: MemorizationMode)

    @StateStrategyType(OneExecutionStateStrategy::class)
    fun showQuranPageSelection()

    fun updatePageValue(pageNumber: Int)

    @StateStrategyType(OneExecutionStateStrategy::class)
    fun showSurahSelection(surahs: List<Surah>)

    fun updateSurahValue(surahNumber: Int, name: String)

    fun updateAyahsRangeValue(range: Pair<Int, Int>)

    @StateStrategyType(OneExecutionStateStrategy::class)
    fun showAyahsRangeSelection(currentAyahsRange: Pair<Int, Int>, maxAyahValue: Int)

    @StateStrategyType(OneExecutionStateStrategy::class)
    fun showRepetitionsSelectionDialog(repetitionCounts: List<Int>)

    fun updateRepetitionsValue(count: Int)

    @StateStrategyType(OneExecutionStateStrategy::class)
    fun showDelayBetweenRepetitionsDialog(delayValues: List<Int>)

    fun updateDelayBetweenRepetitionsValue(value: Int)

    @StateStrategyType(OneExecutionStateStrategy::class)
    fun showRecitationSelectDialog(recitations: List<Recitation>)

    fun updateRecitationValue(name: String)

}
