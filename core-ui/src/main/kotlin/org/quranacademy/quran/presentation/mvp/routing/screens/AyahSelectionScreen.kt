package org.quranacademy.quran.presentation.mvp.routing.screens

import me.aartikov.alligator.Screen
import me.aartikov.alligator.ScreenResult
import org.quranacademy.quran.domain.models.AyahId
import java.io.Serializable

class AyahSelectionScreen(
        val selectedAyah: AyahId,
        val type: Type
) : Screen, Serializable {

    class Result(val ayahId: AyahId, val type: Type) : ScreenResult

    enum class Type {
        START, END
    }

}