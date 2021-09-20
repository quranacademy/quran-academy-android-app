package org.quranacademy.quran.memorization.routing

import me.aartikov.alligator.Screen
import org.quranacademy.quran.memorization.models.MemorizationOptions
import java.io.Serializable

class MemorizationScreen(
        val memorizationOptions: MemorizationOptions
) : Screen, Serializable