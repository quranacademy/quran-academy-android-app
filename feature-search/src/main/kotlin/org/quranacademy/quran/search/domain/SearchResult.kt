package org.quranacademy.quran.search.domain

data class SearchResult(
        val surahNumber: Int,
        val ayahNumber: Int,
        val description: String,
        val text: String
)