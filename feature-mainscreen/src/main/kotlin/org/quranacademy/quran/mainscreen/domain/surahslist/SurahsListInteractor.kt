package org.quranacademy.quran.mainscreen.domain.surahslist

import org.quranacademy.quran.bookmarks.data.readinghistory.LastReadingPlaceInfo
import org.quranacademy.quran.bookmarks.data.readinghistory.ReadingHistoryRepository
import org.quranacademy.quran.data.prefs.AppPreferences
import org.quranacademy.quran.domain.repositories.LanguagesRepository
import org.quranacademy.quran.domain.repositories.SurahsRepository
import javax.inject.Inject

class SurahsListInteractor @Inject constructor(
        private val surahsRepository: SurahsRepository,
        private val languagesRepository: LanguagesRepository,
        private val readingHistoryRepository: ReadingHistoryRepository,
        private val appPreferences: AppPreferences
) {

    fun getLanguageChanges() = languagesRepository.getLanguageChanges()

    suspend fun getSurahsList() = surahsRepository.getSurahsList()

    suspend fun saveLastReadPosition(surahNumber: Int, ayahNumber: Int) {
        val readSettings = appPreferences.getReadSettings()
        readSettings.lastReadSurah = surahNumber
        readSettings.lastReadAyah = ayahNumber
        appPreferences.saveReadSettings(readSettings)

        val recentReadingPlace = LastReadingPlaceInfo(surahNumber, ayahNumber, readSettings.isMushafMode)
        readingHistoryRepository.addRecentReadingPlace(null, recentReadingPlace)
    }

    fun getLastScrollPosition() = appPreferences.getSurahsListLastPosition()

    suspend fun saveLastScrollPosition(position: Int) {
        appPreferences.setSurahsListLastPosition(position)
    }

    suspend fun isMushafMode() = appPreferences.getReadSettings().isMushafMode

}
