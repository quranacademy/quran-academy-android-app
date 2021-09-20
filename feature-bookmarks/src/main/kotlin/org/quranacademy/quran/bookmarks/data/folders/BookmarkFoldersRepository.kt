package org.quranacademy.quran.bookmarks.data.folders

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.withContext
import org.joda.time.DateTime
import org.quranacademy.quran.bookmarks.R
import org.quranacademy.quran.bookmarks.domain.models.BookmarkFolder
import org.quranacademy.quran.data.database.models.BookmarkFolderModel
import org.quranacademy.quran.domain.commons.ResourcesManager
import javax.inject.Inject

class BookmarkFoldersRepository @Inject constructor(
        private val bookmarkFoldersDao: BookmarkFoldersDao,
        private val mapper: BookmarkFolderDatabaseMapper,
        private val resourcesManager: ResourcesManager
) {

    private val updatesChannel = BroadcastChannel<Unit>(1)

    suspend fun getFoldersList(): List<BookmarkFolder> = withContext(Dispatchers.IO) {
        val userFolders = mapper.mapFrom(bookmarkFoldersDao.getAllFolders())
        val defaultFolder = BookmarkFolder(
                id = BookmarkFolder.DEFAULT_FOLDER_ID,
                name = resourcesManager.getString(R.string.bookmarks_favorites_folder),
                time = DateTime(0)
        )
        listOf(defaultFolder, *userFolders.toTypedArray())
    }

    suspend fun deleteFolder(folder: BookmarkFolder) = withContext(Dispatchers.IO) {
        bookmarkFoldersDao.deleteFolder(mapper.mapTo(folder))
        updatesChannel.send(Unit)
    }

    suspend fun createFolder(name: String): BookmarkFolder {
        val model = BookmarkFolderModel(name = name, timestamp = DateTime().millis)
        bookmarkFoldersDao.saveFolder(model)
        updatesChannel.send(Unit)
        return mapper.mapFrom(model)
    }

    suspend fun saveFolder(folder: BookmarkFolder) {
        bookmarkFoldersDao.saveFolder(mapper.mapTo(folder))
        updatesChannel.send(Unit)
    }

    fun getFoldersUpdates() = updatesChannel.asFlow()

}