package org.quranacademy.quran.bookmarks.data.ayahbookmarksrepository

import org.joda.time.DateTime
import org.quranacademy.quran.bookmarks.domain.models.AyahBookmark
import org.quranacademy.quran.bookmarks.domain.models.BookmarkFolder
import org.quranacademy.quran.data.database.daos.SurahsNameTranslationsDao
import org.quranacademy.quran.data.database.models.AyahBookmarkModel
import org.quranacademy.quran.data.database.models.SurahNameTranslationModel
import javax.inject.Inject

class AyahBookmarkDatabaseMapper @Inject constructor(
        private val surahsNameTranslationsDao: SurahsNameTranslationsDao
) {

    fun mapFrom(
            modelPages: List<AyahBookmarkModel>
    ): List<AyahBookmark> {
        val surahNames = surahsNameTranslationsDao.getAllSurahNames()
        val surahsMap = surahNames.map { it.surahNumber to it }.toMap()
        return modelPages.map {
            mapFrom(it, surahsMap)
        }
    }

    fun mapFrom(model: AyahBookmarkModel): AyahBookmark {
        val surahsMap = getSurahNames()
        return mapFrom(model, surahsMap)
    }

    private fun mapFrom(
            model: AyahBookmarkModel,
            surahsMap: Map<Int, SurahNameTranslationModel>
    ): AyahBookmark {
        return AyahBookmark(
                id = model.id,
                surahName = surahsMap[model.surahNumber]!!.transliteratedName,
                surahNumber = model.surahNumber,
                ayahNumber = model.ayahNumber,
                timestamp = DateTime(model.timestamp),
                folderId = model.folderId ?: BookmarkFolder.DEFAULT_FOLDER_ID
        )
    }

    private fun getSurahNames(): Map<Int, SurahNameTranslationModel> {
        val surahNames = surahsNameTranslationsDao.getAllSurahNames()
        return surahNames.map { it.surahNumber to it }.toMap()
    }

    fun mapTo(models: List<AyahBookmark>): List<AyahBookmarkModel> {
        return models.map { mapTo(it) }
    }

    fun mapTo(model: AyahBookmark): AyahBookmarkModel {
        return AyahBookmarkModel(
                id = model.id,
                surahNumber = model.surahNumber,
                ayahNumber = model.ayahNumber,
                timestamp = model.timestamp.millis,
                folderId = model.folderId
        )
    }

}