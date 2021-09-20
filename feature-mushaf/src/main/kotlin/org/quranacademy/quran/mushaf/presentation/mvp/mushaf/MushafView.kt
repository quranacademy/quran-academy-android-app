package org.quranacademy.quran.mushaf.presentation.mvp.mushaf

import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy
import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import org.quranacademy.quran.domain.models.*
import org.quranacademy.quran.domain.models.bounds.AyahBounds
import org.quranacademy.quran.presentation.mvp.BaseMvpView

@StateStrategyType(AddToEndSingleStrategy::class)
interface MushafView : BaseMvpView {

    fun showImagesBundleDownloadSuggestion(isVisible: Boolean)

    fun showImagesBundleDownloadProgress(isVisible: Boolean)

    fun updateImagesBundleDownloadProgress(downloadInfo: FileDownloadInfo)

    fun showQuranPager()

    fun switchToPage(pageNumber: Int)

    fun showToolbar(isVisible: Boolean)

    fun showPageInfo(page: QuranPage)

    fun showAyahToolbar(ayahDetails: AyahDetails, ayahBounds: List<AyahBounds>)

    fun hideAyahToolbar()

    @StateStrategyType(OneExecutionStateStrategy::class)
    fun showAyahTranslations(ayahIds: List<AyahId>)

    fun closeTranslationPanel()

    @StateStrategyType(OneExecutionStateStrategy::class)
    fun showPlayerOptionsPanel(startAyah: AyahId, endAyah: AyahId)

    fun hidePlayerOptionsPanel()

    @StateStrategyType(OneExecutionStateStrategy::class)
    fun showPlayerControlPanel()

    fun hidePlayerControlPanel()

    @StateStrategyType(OneExecutionStateStrategy::class)
    fun showMushafThemeSelectingDialog()

    fun setHorizontalMode(isEnabled: Boolean)

}