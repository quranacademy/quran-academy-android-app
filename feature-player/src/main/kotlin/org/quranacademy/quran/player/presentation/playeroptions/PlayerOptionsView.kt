package org.quranacademy.quran.player.presentation.playeroptions

import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import org.quranacademy.quran.domain.models.AyahId
import org.quranacademy.quran.domain.models.FileDownloadInfo
import org.quranacademy.quran.presentation.mvp.BaseMvpView

@StateStrategyType(AddToEndSingleStrategy::class)
interface PlayerOptionsView : BaseMvpView {

    fun showRecitationsLoadingProgress(isVisible: Boolean)

    fun showPlayerOptions(
            recitationName: String,
            autoScrollEnabled: Boolean,
            showHighlightWordsOption: Boolean,
            highlightWords: Boolean,
            startSurahName: String,
            startAyah: AyahId,
            endSurahName: String,
            endAyah: AyahId,
            rangeRepetitionsCount: Int,
            ayahRepetitionsCount: Int
    )

    fun showRecitationsLoadingError(isVisible: Boolean)

    fun showTimecodesDownloadingNeededDialog()

    fun showTimecodesDownloadingProgress(isVisible: Boolean)

    fun updateTimecodesDownloadingProgress(progress: FileDownloadInfo)

    fun showRangeRepetitionsSelectionDialog(count: Int)

    fun showAyahRepetitionsSelectionDialog(count: Int)

}