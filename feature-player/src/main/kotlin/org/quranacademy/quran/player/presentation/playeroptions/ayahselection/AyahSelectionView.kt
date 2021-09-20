package org.quranacademy.quran.player.presentation.playeroptions.ayahselection

import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import org.quranacademy.quran.domain.models.Surah
import org.quranacademy.quran.presentation.mvp.BaseMvpView

@StateStrategyType(AddToEndSingleStrategy::class)
interface AyahSelectionView : BaseMvpView {

    fun setTitle(title: String)

    fun showInitialData(
            surahs: List<Surah>,
            ayahsCount: Int,
            currentSurah: Surah,
            currentAyahNumber: Int
    )
    fun updateAyahValues(ayahsCount: Int)

}