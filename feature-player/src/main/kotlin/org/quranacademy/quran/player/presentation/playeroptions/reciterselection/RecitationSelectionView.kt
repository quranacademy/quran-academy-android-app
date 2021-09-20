package org.quranacademy.quran.player.presentation.playeroptions.reciterselection

import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import org.quranacademy.quran.domain.models.Recitation
import org.quranacademy.quran.presentation.mvp.BaseMvpView

@StateStrategyType(AddToEndSingleStrategy::class)
interface RecitationSelectionView : BaseMvpView {

    fun showRecitations(
            recitations: List<Recitation>
    )
}