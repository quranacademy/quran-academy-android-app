package org.quranacademy.quran.player.presentation.playercontrol.dynamicsettings

import me.aartikov.alligator.Screen
import me.aartikov.alligator.ScreenResult
import org.quranacademy.quran.domain.models.PlaybackOptions

class PlayerDynamicSettingsScreen : Screen {

    class Result(val options: PlaybackOptions?) : ScreenResult

}