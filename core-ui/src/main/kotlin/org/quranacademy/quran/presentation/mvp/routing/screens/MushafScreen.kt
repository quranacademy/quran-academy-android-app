package org.quranacademy.quran.presentation.mvp.routing.screens

import me.aartikov.alligator.Screen
import org.quranacademy.quran.domain.models.AyahId
import java.io.Serializable

class MushafScreen(
        val ayahForHighlighting: AyahId? = null //при переходе к нужной странице, подсвечиваем нужный аят
) : Screen, Serializable