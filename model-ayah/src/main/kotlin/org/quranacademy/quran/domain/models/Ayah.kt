package org.quranacademy.quran.domain.models

data class Ayah(
        val surahNumber: Int,
        val ayahNumber: Int,
        val arabicText: String,
        val arabicTextNewUthmanicHafs: String,
        val arabicTextTajweed: String,
        val isSajdaAyah: Boolean
) {

    val translations = mutableListOf<AyahTranslation>()
    lateinit var words: List<AyahWordItem>
    var isWordByWordEnabled: Boolean = false

}