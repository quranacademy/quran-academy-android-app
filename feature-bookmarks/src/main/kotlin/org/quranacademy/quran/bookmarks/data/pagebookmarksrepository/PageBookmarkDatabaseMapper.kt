package org.quranacademy.quran.bookmarks.data.pagebookmarksrepository

import org.joda.time.DateTime
import org.quranacademy.quran.QuranConstants
import org.quranacademy.quran.bookmarks.domain.models.PageBookmark
import org.quranacademy.quran.data.database.models.PageBookmarkModel
import org.quranacademy.quran.data.database.models.SurahNameTranslationModel
import javax.inject.Inject

class PageBookmarkDatabaseMapper @Inject constructor() {

    fun mapFrom(models: List<PageBookmarkModel>, surahs: List<SurahNameTranslationModel>): List<PageBookmark> {
        val surahsMap = surahs.map { it.surahNumber to it }.toMap()
        return models.map {
            val surahNumber = QuranConstants.SURAH_FOR_PAGE[it.pageNumber - 1]
            PageBookmark(
                    id = it.id,
                    surahName = surahsMap[surahNumber]!!.transliteratedName,
                    pageNumber = it.pageNumber,
                    timestamp = DateTime(it.timestamp)
            )
        }
    }

    fun mapTo(models: List<PageBookmark>): List<PageBookmarkModel> {
        return models.map { mapTo(it) }
    }

    fun mapTo(it: PageBookmark): PageBookmarkModel {
        return PageBookmarkModel(
                id = it.id,
                pageNumber = it.pageNumber,
                timestamp = it.timestamp.millis

        )
    }

}