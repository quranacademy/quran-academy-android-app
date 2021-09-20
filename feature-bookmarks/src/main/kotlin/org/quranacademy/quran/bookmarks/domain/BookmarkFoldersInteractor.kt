package org.quranacademy.quran.bookmarks.domain

import org.quranacademy.quran.bookmarks.data.ayahbookmarksrepository.AyahBookmarksRepository
import org.quranacademy.quran.bookmarks.data.folders.BookmarkFoldersRepository
import org.quranacademy.quran.bookmarks.domain.models.BookmarkFolder
import org.quranacademy.quran.domain.models.AyahId
import javax.inject.Inject

class BookmarkFoldersInteractor @Inject constructor(
        private val ayahBookmarksRepository: AyahBookmarksRepository,
        private val bookmarkFoldersRepository: BookmarkFoldersRepository
) {

    suspend fun getAyahFolderId(ayahId: AyahId): Long {
        val ayahBookmark = ayahBookmarksRepository.getAyahBookmark(ayahId)
                ?: return BookmarkFolder.DEFAULT_FOLDER_ID
        return bookmarkFoldersRepository.getFoldersList()
                .firstOrNull { it.id == ayahBookmark.folderId }
                ?.id ?: BookmarkFolder.DEFAULT_FOLDER_ID
    }

    suspend fun getBookmarkFolders(): List<BookmarkFolder> {
        return bookmarkFoldersRepository.getFoldersList()
    }

    suspend fun addBookmark(ayahId: AyahId, folder: BookmarkFolder) {
        ayahBookmarksRepository.bookmarkAyah(ayahId, folder.id)
    }

    suspend fun createFolder(name: String): BookmarkFolder {
        return bookmarkFoldersRepository.createFolder(name)
    }

}
