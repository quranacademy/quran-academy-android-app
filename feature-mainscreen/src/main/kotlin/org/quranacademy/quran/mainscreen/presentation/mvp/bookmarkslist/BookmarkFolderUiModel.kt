package org.quranacademy.quran.mainscreen.presentation.mvp.bookmarkslist

import org.joda.time.DateTime
import org.quranacademy.quran.bookmarks.domain.models.BookmarkItem

data class BookmarkFolderUiModel(
        val id: Long,
        val name: String,
        val time: DateTime,
        val bookmarkCount: Int,
        val isDefaultFolder: Boolean,
        val isExpanded: Boolean
) : BookmarkItem