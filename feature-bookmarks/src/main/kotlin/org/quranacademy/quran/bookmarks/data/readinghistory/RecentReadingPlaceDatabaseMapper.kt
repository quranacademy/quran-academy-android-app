package org.quranacademy.quran.bookmarks.data.readinghistory

import org.joda.time.DateTime
import org.quranacademy.quran.bookmarks.domain.models.RecentReadingPlace
import org.quranacademy.quran.data.database.models.RecentReadingPlaceModel
import org.quranacademy.quran.data.database.models.SurahNameTranslationModel
import org.quranacademy.quran.domain.AyahPageFinder
import javax.inject.Inject

class RecentReadingPlaceDatabaseMapper @Inject constructor(
        private val ayahPageFinder: AyahPageFinder
) {

    suspend fun mapFrom(
            models: List<RecentReadingPlaceModel>,
            surahs: List<SurahNameTranslationModel>
    ): List<RecentReadingPlace> {
        val surahsMap = surahs.map { it.surahNumber to it }.toMap()
        return models.map {
            RecentReadingPlace(
                    id = it.id,
                    surahName = surahsMap[it.surahNumber]!!.transliteratedName,
                    surahNumber = it.surahNumber,
                    ayahNumber = it.ayahNumber,
                    pageNumber = ayahPageFinder.getPageForAyah(it.surahNumber, it.ayahNumber),
                    isMushafMode = it.isMushafMode,
                    timestamp = DateTime(it.timestamp)
            )
        }
    }

}