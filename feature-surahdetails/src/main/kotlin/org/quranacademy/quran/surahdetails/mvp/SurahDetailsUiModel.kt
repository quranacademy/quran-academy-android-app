package org.quranacademy.quran.surahdetails.mvp

class SurahDetailsUiModel(
        val id: Long,
        val surahNumber: Int,
        val pageNumber: Int,
        val showPageNumber: Boolean,
        val bismillahPre: Boolean,
        val arabicName: String,
        val transliteratedName: String,
        val translatedName: String,
        val ayahsCount: Int,
        val ayahs: List<AyahUiModel>
)