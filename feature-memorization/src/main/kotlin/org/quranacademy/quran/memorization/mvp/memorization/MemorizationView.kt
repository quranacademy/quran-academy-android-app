package org.quranacademy.quran.memorization.mvp.memorization

import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import org.quranacademy.quran.domain.models.AyahId
import org.quranacademy.quran.domain.models.QuranPage
import org.quranacademy.quran.mushaf.presentation.mvp.mushafpage.AyahHighlightType
import org.quranacademy.quran.presentation.mvp.BaseMvpView
import java.util.*

@StateStrategyType(AddToEndSingleStrategy::class)
interface MemorizationView : BaseMvpView {

    fun showDataDownloadDialog(isVisible: Boolean)

    fun showNoNetworkDialog()

    fun showToolbar(isVisible: Boolean)

    fun showPage(pageInfo: QuranPage)

    fun updateAyahHighlighting(ayahHighlights: SortedMap<AyahHighlightType, MutableSet<AyahId>>)

    fun updatePlayPauseState(isPlaying: Boolean)

    fun showMemorizationCompleted()

    fun showExitConfirmDialog()

}
