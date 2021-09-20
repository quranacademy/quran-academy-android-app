package org.quranacademy.quran.bookmarks.data.readinghistory

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.withContext
import org.joda.time.DateTime
import org.quranacademy.quran.bookmarks.domain.models.RecentReadingPlace
import org.quranacademy.quran.data.database.daos.RecentReadingPlacesDao
import org.quranacademy.quran.data.database.daos.SurahsNameTranslationsDao
import org.quranacademy.quran.data.database.models.RecentReadingPlaceModel
import javax.inject.Inject

class ReadingHistoryRepository @Inject constructor(
        private val recentReadingPlacesDao: RecentReadingPlacesDao,
        private val surahsNameTranslationsDao: SurahsNameTranslationsDao,
        private val recentReadingPlaceDatabaseMapper: RecentReadingPlaceDatabaseMapper
) {

    private val updatesChannel = BroadcastChannel<Unit>(1)

    fun getReadingHistoryUpdates() = updatesChannel.asFlow()

    suspend fun getRecentReadingPlaces(): List<RecentReadingPlace> = withContext(Dispatchers.IO) {
        val recentReadingPlaces = recentReadingPlacesDao.getRecentReadingPlaces()
        val surahNames = surahsNameTranslationsDao.getAllSurahNames()
        recentReadingPlaceDatabaseMapper.mapFrom(recentReadingPlaces, surahNames)
    }

    suspend fun addRecentReadingPlace(
            previousReadingPlace: LastReadingPlaceInfo?,
            newReadingPlace: LastReadingPlaceInfo
    ) = withContext(Dispatchers.IO) {
        //удаляем старое место чтения
        previousReadingPlace?.let { recentReadingPlacesDao.removeFromHistory(it.surahNumber, it.ayahNumber) }
        //если мы имеем такую же запись, то удаляем её, чтобы не дублировать место чтения
        recentReadingPlacesDao.removeFromHistory(newReadingPlace.surahNumber, newReadingPlace.ayahNumber)

        val recentReadingPlace = RecentReadingPlaceModel(
                surahNumber = newReadingPlace.surahNumber,
                ayahNumber = newReadingPlace.ayahNumber,
                isMushafMode = newReadingPlace.isMushafMode,
                timestamp = DateTime.now().millis
        )
        recentReadingPlacesDao.addToHistory(recentReadingPlace)
        updatesChannel.send(Unit)
    }


}