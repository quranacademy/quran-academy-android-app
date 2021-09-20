package org.quranacademy.quran.domain.models

import java.io.File
import java.io.Serializable

data class AyahAudio(
        val surahNumber: Int,
        val ayahNumber: Int,
        val surahName: String,
        val duration: Long,
        val audioFile: File,
        val recitation: Recitation
) : Serializable {

    companion object {
        const val UNKNOWN_DURATION = 0L
    }

}