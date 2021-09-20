package org.quranacademy.quran.settings.presentation.mvp.mushaftype

import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import org.quranacademy.quran.domain.models.FileDownloadInfo
import org.quranacademy.quran.presentation.mvp.BaseMvpView

@StateStrategyType(AddToEndSingleStrategy::class)
interface MushafTypeSelectionView : BaseMvpView {

    fun showPageTypes(types: List<PageTypeInfo>)

    fun showMushafBoundsDownloadDialog(show: Boolean)

    fun updateMushafBoundsDownloadProgress(progress: FileDownloadInfo)

    fun showBoundsDownloadRetryDialog(info: PageTypeInfo)

}