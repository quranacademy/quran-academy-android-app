package org.quranacademy.quran.surahdetails.mvp

import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy
import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import org.quranacademy.quran.domain.models.AyahId
import org.quranacademy.quran.presentation.mvp.BaseMvpView

@StateStrategyType(AddToEndSingleStrategy::class)
interface SurahDetailsView : BaseMvpView {

    fun showProgressLayout(isVisible: Boolean)

    @StateStrategyType(OneExecutionStateStrategy::class)
    fun openTextSettingsPanelClicked(isOpened: Boolean)

    fun showSurah(surah: SurahDetailsUiModel, ayahNumber: Int)

    fun highlightNowPlayingAyah(ayahNumber: Int?)

    fun scrollToAyah(ayahNumber: Int)

    fun updateCurrentPositionInfo(pageNumber: Int, juzNumber: Int)

    fun highlightNowPlayingWord(wordNumber: Int?)

    fun showPlayerButton(isShowing: Boolean)

    @StateStrategyType(OneExecutionStateStrategy::class)
    fun showPlayerOptionsPanel(startAyah: AyahId, endAyah: AyahId)

    fun showPlayerControlPanel()

    fun hidePlayerControlPanel()

}