package org.quranacademy.quran.domain.models.bounds

data class AyahMarkerPosition(
        val surahNumber: Int,
        val ayahNumber: Int,
        val pageNumber: Int,
        var x: Int,
        var y: Int
)