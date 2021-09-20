package org.quranacademy.quran.bookmarks.data.folders

import org.joda.time.DateTime
import org.quranacademy.quran.bookmarks.domain.models.BookmarkFolder
import org.quranacademy.quran.data.database.models.BookmarkFolderModel
import javax.inject.Inject

class BookmarkFolderDatabaseMapper @Inject constructor() {

    fun mapFrom(modelPages: List<BookmarkFolderModel>): List<BookmarkFolder> {
        return modelPages.map {
            mapFrom(it)
        }
    }

    fun mapFrom(it: BookmarkFolderModel): BookmarkFolder {
        return BookmarkFolder(
                id = it.id,
                name = it.name,
                time = DateTime(it.timestamp)
        )
    }

    fun mapTo(models: List<BookmarkFolder>): List<BookmarkFolderModel> {
        return models.map { mapTo(it) }
    }

    fun mapTo(model: BookmarkFolder): BookmarkFolderModel {
        return BookmarkFolderModel(
                id = model.id,
                name = model.name,
                timestamp = model.time.millis
        )
    }

}