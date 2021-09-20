package org.quranacademy.quran.presentation.mvp.routing.screens

import me.aartikov.alligator.Screen
import org.quranacademy.quran.domain.models.AyahId
import java.io.Serializable

data class AyahDetailsScreen(val ayahId: AyahId) : Screen, Serializable