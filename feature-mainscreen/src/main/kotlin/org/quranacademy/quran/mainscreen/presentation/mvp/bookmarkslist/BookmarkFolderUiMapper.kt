package org.quranacademy.quran.mainscreen.presentation.mvp.bookmarkslist

import org.quranacademy.quran.bookmarks.domain.models.AyahBookmark
import org.quranacademy.quran.bookmarks.domain.models.BookmarkFolder
import javax.inject.Inject

class BookmarkFolderUiMapper @Inject constructor() {

    fun mapTo(
            ayahBookmark: List<AyahBookmark>,
            folders: List<BookmarkFolder>,
            oldFolders: List<BookmarkFolderUiModel>? = null
    ): List<BookmarkFolderUiModel> {
        return folders.map { folder ->
            val bookmarkCount = ayahBookmark.count {
                it.folderId == folder.id
            }
            mapTo(folder, bookmarkCount, oldFolders)
        }
    }

    private fun mapTo(
            folder: BookmarkFolder,
            bookmarkCount: Int,
            oldFolders: List<BookmarkFolderUiModel>?
    ): BookmarkFolderUiModel {
        val isDefaultFolder = folder.id == BookmarkFolder.DEFAULT_FOLDER_ID
        val isExpanded = oldFolders?.firstOrNull {
            it.id == folder.id
        }?.isExpanded ?: isDefaultFolder
        return BookmarkFolderUiModel(
                id = folder.id,
                name = folder.name,
                time = folder.time,
                bookmarkCount = bookmarkCount,
                isDefaultFolder = isDefaultFolder,
                isExpanded = isExpanded
        )
    }

    fun mapFrom(models: List<BookmarkFolderUiModel>): List<BookmarkFolder> {
        return models.map { folder -> mapFrom(folder) }
    }

    fun mapFrom(folder: BookmarkFolderUiModel): BookmarkFolder {
        return BookmarkFolder(
                id = folder.id,
                name = folder.name,
                time = folder.time
        )
    }

}