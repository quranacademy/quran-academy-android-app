package org.quranacademy.quran.domain.models

import java.io.Serializable

data class PlaybackOptions(
        val rangeRepeatCount: Int,
        val ayahRepeatCount: Int,
        val recitation: Recitation,
        val autoScrollEnabled: Boolean,
        val highlightWords: Boolean
) : Serializable