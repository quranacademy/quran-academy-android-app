package org.quranacademy.quran.domain.models

import java.io.Serializable

data class PlaybackRequest(
        val verseRange: VerseRange,
        val rangeRepetitionCount: Int,
        val ayahRepetitionCount: Int,
        val recitation: Recitation,
        val autoScrollEnabled: Boolean,
        val highlightWords: Boolean,
        val currentPlaybackPosition: Int = 0
) : Serializable