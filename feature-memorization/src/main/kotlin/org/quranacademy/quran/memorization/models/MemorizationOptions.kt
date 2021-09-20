package org.quranacademy.quran.memorization.models

import org.quranacademy.quran.domain.models.Recitation
import java.io.Serializable

data class MemorizationOptions(
        val mode: MemorizationMode,
        val modeData: ModeData,
        val repetitionsCount: Int,
        val delayBetweenRepetitions: Int,
        val recitation: Recitation
) : Serializable

interface ModeData : Serializable

data class PageModeData(
        val pageNumber: Int
) : ModeData

data class SurahModeData(
        val surahNumber: Int,
        val ayahsRange: Pair<Int, Int>
) : ModeData