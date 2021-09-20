package org.quranacademy.quran.domain.models

import java.io.Serializable

data class AyahWordId(
        val surahNumber: Int,
        val ayahNumber: Int,
        val wordIndex: Int
) : Serializable