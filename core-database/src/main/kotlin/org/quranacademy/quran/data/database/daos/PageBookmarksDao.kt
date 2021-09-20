package org.quranacademy.quran.data.database.daos

import com.raizlabs.android.dbflow.sql.language.SQLite
import org.quranacademy.quran.data.database.models.PageBookmarkModel
import org.quranacademy.quran.data.database.models.PageBookmarkModel_Table
import javax.inject.Inject

class PageBookmarksDao @Inject constructor() {

    fun getAllBookmarks(): List<PageBookmarkModel> {
        return SQLite.select().from(PageBookmarkModel::class.java).queryList()
    }

    fun isPageBookmarked(pageNumber: Int): Boolean {
        return SQLite.select().from(PageBookmarkModel::class.java)
                .where(PageBookmarkModel_Table.page_number.`is`(pageNumber))
                .count() > 0
    }

    fun deleteBookmark(pageNumber: Int) {
        SQLite.delete().from(PageBookmarkModel::class.java)
                .where(PageBookmarkModel_Table.page_number.`is`(pageNumber))
                .execute()
    }

    fun deleteBookmarks(bookmarks: List<PageBookmarkModel>) {
        val bookmarksIds = bookmarks.map { it.id }
        SQLite.delete().from(PageBookmarkModel::class.java)
                .where(PageBookmarkModel_Table.id.`in`(bookmarksIds))
                .execute()
    }

}