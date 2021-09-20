package org.quranacademy.quran.memorization.routing

import me.aartikov.alligator.Screen
import org.quranacademy.quran.domain.models.AyahId

class MemorizationResultsScreen(
        ayahs: List<AyahId>,
        forgottenAyahs: List<AyahId>
) : Screen