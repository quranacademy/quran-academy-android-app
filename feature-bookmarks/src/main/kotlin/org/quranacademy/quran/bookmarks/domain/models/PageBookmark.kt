package org.quranacademy.quran.bookmarks.domain.models

import org.joda.time.DateTime

data class PageBookmark(
        val id: Long,
        val surahName: String,
        val pageNumber: Int,
        val timestamp: DateTime
) : BookmarkItem