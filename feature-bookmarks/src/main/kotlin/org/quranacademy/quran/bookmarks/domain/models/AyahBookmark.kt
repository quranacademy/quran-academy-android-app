package org.quranacademy.quran.bookmarks.domain.models

import org.joda.time.DateTime

data class AyahBookmark(
        val id: Long,
        val surahName: String,
        val surahNumber: Int,
        val ayahNumber: Int,
        val timestamp: DateTime,
        val folderId: Long
) : BookmarkItem