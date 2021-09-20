package org.quranacademy.quran.mushaf.presentation.mvp.mushafpage

import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import org.quranacademy.quran.domain.models.AyahId
import org.quranacademy.quran.domain.models.FileDownloadInfo
import org.quranacademy.quran.domain.models.QuranPage
import org.quranacademy.quran.presentation.mvp.BaseMvpView
import java.util.*

@StateStrategyType(AddToEndSingleStrategy::class)
interface MushafPageView : BaseMvpView {

    fun showImageDownloadProgress(isVisible: Boolean)

    fun updateImageDownloadProgress(downloadInfo: FileDownloadInfo)

    fun showImageDownloadError(isVisible: Boolean)

    fun showPage(pageInfo: QuranPage)

    fun updateAyahHighlighting(ayahHighlights: SortedMap<AyahHighlightType, MutableSet<AyahId>>)

}