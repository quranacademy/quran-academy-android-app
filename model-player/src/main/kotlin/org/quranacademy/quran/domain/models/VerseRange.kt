package org.quranacademy.quran.domain.models

import java.io.Serializable

data class VerseRange(
        val startSurah: Int,
        val startAyah: Int,
        val endingSurah: Int,
        val endingAyah: Int
) : Serializable