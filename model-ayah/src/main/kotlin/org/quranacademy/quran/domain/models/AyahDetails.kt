package org.quranacademy.quran.domain.models

data class AyahDetails(
        val surahNumber: Int,
        val ayahNumber: Int,
        val arabicText: String,
        val arabicTextTajweed: String,
        val surahName: String,
        val translations: List<AyahTranslation>,
        val words: List<AyahWordItem>?,
        val isBookmarked: Boolean
) {

    val id = AyahId(surahNumber, ayahNumber)

}