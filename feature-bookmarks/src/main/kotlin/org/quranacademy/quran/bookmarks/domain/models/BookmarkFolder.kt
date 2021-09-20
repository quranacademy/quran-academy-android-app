package org.quranacademy.quran.bookmarks.domain.models

import org.joda.time.DateTime

data class BookmarkFolder(
        val id: Long,
        val name: String,
        val time: DateTime
) : BookmarkItem {

    companion object {
        const val DEFAULT_FOLDER_ID = -1L
    }

}