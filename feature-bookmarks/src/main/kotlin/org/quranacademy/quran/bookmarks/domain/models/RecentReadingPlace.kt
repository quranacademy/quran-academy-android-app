package org.quranacademy.quran.bookmarks.domain.models

import org.joda.time.DateTime

data class RecentReadingPlace(
        val id: Long,
        val surahName: String,
        val surahNumber: Int,
        val ayahNumber: Int,
        val pageNumber: Int,
        val isMushafMode: Boolean,
        val timestamp: DateTime
) : BookmarkItem