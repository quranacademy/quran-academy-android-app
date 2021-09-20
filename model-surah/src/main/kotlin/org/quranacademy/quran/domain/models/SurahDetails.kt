package org.quranacademy.quran.domain.models

data class SurahDetails(
        val id: Long,
        val surahNumber: Int,
        val pageNumber: Int,
        val bismillahPre: Boolean,
        val arabicName: String,
        val transliteratedName: String,
        val translatedName: String,
        val ayahsCount: Int,
        val ayahs: List<Ayah>
)