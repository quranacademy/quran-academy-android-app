package org.quranacademy.quran.domain.models.bounds

data class SurahHeaderBounds(
        val surahNumber: Int,
        val pageNumber: Int,
        val x: Int,
        val y: Int,
        val width: Int,
        val height: Int
)