package org.quranacademy.quran.player.presentation.playercontrol.dynamicsettings

import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import org.quranacademy.quran.presentation.mvp.BaseMvpView

@StateStrategyType(AddToEndSingleStrategy::class)
interface PlayerDynamicSettingsView : BaseMvpView {

    fun showPlaybackSettings(
            recitationName: String,
            autoScrollEnabled: Boolean,
            rangeRepetitionsCount: Int,
            ayahRepetitionsCount: Int
    )

    fun showRangeRepetitionsSelectionDialog(count: Int)

    fun showAyahRepetitionsSelectionDialog(count: Int)

}