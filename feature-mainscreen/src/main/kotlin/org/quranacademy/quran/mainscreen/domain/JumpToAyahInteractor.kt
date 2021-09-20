package org.quranacademy.quran.mainscreen.domain

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.quranacademy.quran.data.prefs.AppPreferences
import org.quranacademy.quran.domain.AyahPageFinder
import org.quranacademy.quran.domain.models.Surah
import org.quranacademy.quran.domain.repositories.SurahsRepository
import javax.inject.Inject

class JumpToAyahInteractor @Inject constructor(
        private val appPreferences: AppPreferences,
        private val surahsRepository: SurahsRepository,
        private val ayahPageFinder: AyahPageFinder
) {

    suspend fun getSurahsList(): List<Surah> {
        return surahsRepository.getSurahsList()
    }

    suspend fun getAyahPageNumber(surahNumber: Int, ayahNumber: Int): Int {
        return ayahPageFinder.getPageForAyah(surahNumber, ayahNumber)
    }

    suspend fun setLastReadPosition(
            surahNumber: Int,
            ayahNumber: Int,
            isMushafMode: Boolean
    ) = withContext(Dispatchers.IO) {
        val readSettings = appPreferences.getReadSettings()
        readSettings.lastReadSurah = surahNumber
        readSettings.lastReadAyah = ayahNumber
        readSettings.isMushafMode = isMushafMode
        appPreferences.saveReadSettings(readSettings)
    }

}
