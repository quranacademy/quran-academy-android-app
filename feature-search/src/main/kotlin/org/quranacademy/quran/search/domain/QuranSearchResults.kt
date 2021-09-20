package org.quranacademy.quran.search.domain

data class QuranSearchResults(
        val query: String,
        val results: List<SearchResult>,
        val isArabic: Boolean
)