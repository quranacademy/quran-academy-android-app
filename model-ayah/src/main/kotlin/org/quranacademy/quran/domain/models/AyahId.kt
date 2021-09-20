package org.quranacademy.quran.domain.models

import java.io.Serializable

data class AyahId(
        val surahNumber: Int,
        val ayahNumber: Int
) : Serializable {

    fun compareTo(other: AyahId): Int {
        return when {
            surahNumber > other.surahNumber -> 1
            surahNumber < other.surahNumber -> -1
            else ->
                when {
                    ayahNumber > other.ayahNumber -> 1
                    ayahNumber < other.ayahNumber -> -1
                    else -> 0
                }
        }
    }

}