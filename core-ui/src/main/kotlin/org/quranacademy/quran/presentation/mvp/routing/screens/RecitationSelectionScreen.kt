package org.quranacademy.quran.presentation.mvp.routing.screens

import me.aartikov.alligator.Screen
import me.aartikov.alligator.ScreenResult
import org.quranacademy.quran.domain.models.Recitation
import java.io.Serializable

class RecitationSelectionScreen : Screen, Serializable {

    class Result(val recitation: Recitation) : ScreenResult

}