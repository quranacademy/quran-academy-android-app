package org.quranacademy.quran.bookmarks.data.folders

import com.raizlabs.android.dbflow.config.FlowManager
import com.raizlabs.android.dbflow.sql.language.SQLite
import org.quranacademy.quran.data.database.models.BookmarkFolderModel
import javax.inject.Inject

class BookmarkFoldersDao @Inject constructor() {

    private val adapter by lazy { FlowManager.getModelAdapter(BookmarkFolderModel::class.java) }

    fun getAllFolders(): List<BookmarkFolderModel> {
        return SQLite.select().from(BookmarkFolderModel::class.java).queryList()
    }

    fun saveFolder(folder: BookmarkFolderModel) {
        adapter.save(folder)
    }

    fun deleteFolder(folder: BookmarkFolderModel) {
        adapter.delete(folder)
    }

}