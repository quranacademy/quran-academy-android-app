package org.quranacademy.quran.surahdetails.domain

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import org.quranacademy.quran.bookmarks.data.readinghistory.LastReadingPlaceInfo
import org.quranacademy.quran.bookmarks.data.readinghistory.ReadingHistoryRepository
import org.quranacademy.quran.data.prefs.AppPreferences
import org.quranacademy.quran.domain.models.AyahId
import org.quranacademy.quran.domain.models.SurahDetails
import org.quranacademy.quran.domain.repositories.SurahsRepository
import org.quranacademy.quran.domain.repositories.TranslationsOrderRepository
import org.quranacademy.quran.domain.repositories.TranslationsRepository
import org.quranacademy.quran.domain.repositories.WordByWordTranslationsRepository
import org.quranacademy.quran.extensions.mergeWith
import javax.inject.Inject

class SurahDetailsInteractor @Inject constructor(
        private val surahsRepository: SurahsRepository,
        private val translationsRepository: TranslationsRepository,
        private val translationsOrderRepository: TranslationsOrderRepository,
        private val wordByWordTranslationsRepository: WordByWordTranslationsRepository,
        private val readingHistoryRepository: ReadingHistoryRepository,
        private val appPreferences: AppPreferences
) {

    suspend fun getTranslationsListChangedUpdates(): Flow<Unit> {
        return translationsRepository.getEnabledTranslationsListUpdates()
                .mergeWith(translationsOrderRepository.getTranslationOrderUpdates())
                .mergeWith(wordByWordTranslationsRepository.getEnabledTranslationsListUpdates())
                .mergeWith(wordByWordTranslationsRepository.getTranslationsListUpdates())
                .mergeWith(appPreferences.getWbwEnablingUpdates().map { Unit })

    }

    suspend fun getSurahDetails(surahNumber: Int): SurahDetails = surahsRepository.getSurahDetails(surahNumber)

    fun getLastReadPosition(): AyahId {
        val readSettings = appPreferences.getReadSettings()
        return AyahId(readSettings.lastReadSurah, readSettings.lastReadAyah)
    }

    suspend fun saveLastReadPosition(surahNumber: Int, ayahNumber: Int, isMushafMode: Boolean = false) = withContext(Dispatchers.IO) {
        saveRecentPlace(surahNumber, ayahNumber, isMushafMode)

        val readSettings = appPreferences.getReadSettings()
        readSettings.isMushafMode = isMushafMode
        readSettings.lastReadSurah = surahNumber
        readSettings.lastReadAyah = ayahNumber
        appPreferences.saveReadSettings(readSettings)
    }

    private suspend fun saveRecentPlace(surahNumber: Int, ayahNumber: Int, isMushafMode: Boolean) {
        val previousPage = appPreferences.getReadSettings().let {
            LastReadingPlaceInfo(
                    surahNumber = it.lastReadSurah,
                    ayahNumber = it.lastReadAyah,
                    isMushafMode = it.isMushafMode
            )
        }

        val newPage = LastReadingPlaceInfo(
                surahNumber = surahNumber,
                ayahNumber = ayahNumber,
                isMushafMode = isMushafMode
        )
        readingHistoryRepository.addRecentReadingPlace(previousPage, newPage)
    }

}
