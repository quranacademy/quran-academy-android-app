package org.quranacademy.quran.domain.models

data class QuranPage(
        val pageNumber: Int,
        val imagePath: String,
        val isImageExists: Boolean,
        val firstAyahNumber: Int,
        val surahNumber: Int,
        val surahName: String,
        val juzNumber: Int,
        val rubNumber: Int,
        val surahTranslatedName: String,
        val isBookmarked: Boolean,
        val pageBounds: PageBounds
)