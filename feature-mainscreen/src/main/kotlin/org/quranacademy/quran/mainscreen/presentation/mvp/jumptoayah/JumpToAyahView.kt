package org.quranacademy.quran.mainscreen.presentation.mvp.jumptoayah

import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import org.quranacademy.quran.domain.models.Surah
import org.quranacademy.quran.presentation.mvp.BaseMvpView

@StateStrategyType(AddToEndSingleStrategy::class)
interface JumpToAyahView : BaseMvpView {

    fun showSurahsList(surahs: List<Surah>)

    fun setCurrentSurahAndAyah(surahNumber: Int, selectedAyah: Int)

    fun configureAyahSelector(ayahsCount: Int, currentAyah: Int = 1)

    fun showPageNumberHint(pageNumber: Int)

    fun showReadModeSelectionDialog()

}