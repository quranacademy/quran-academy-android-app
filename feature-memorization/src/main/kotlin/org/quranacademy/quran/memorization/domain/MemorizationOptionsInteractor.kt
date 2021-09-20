package org.quranacademy.quran.memorization.domain

import org.quranacademy.quran.QuranConstants
import org.quranacademy.quran.domain.AyahPageFinder
import org.quranacademy.quran.domain.models.Surah
import org.quranacademy.quran.domain.repositories.SurahsRepository
import org.quranacademy.quran.domain.repositories.WordByWordTranslationsRepository
import org.quranacademy.quran.recitationsrepository.RecitationsRepository
import javax.inject.Inject

class MemorizationOptionsInteractor @Inject constructor(
        private val wordByWordTranslationsRepository: WordByWordTranslationsRepository,
        private val recitationsRepository: RecitationsRepository,
        private val surahsRepository: SurahsRepository,
        private val ayahPageFinder: AyahPageFinder
) {

    suspend fun getTranslationsList() = wordByWordTranslationsRepository.getTranslationsList(false)

    suspend fun getRecitations() = recitationsRepository.getRecitations()

    suspend fun getSurahs(): List<Surah> = surahsRepository.getSurahsList()

    fun getPageForSurah(surahNumber: Int) = QuranConstants.PAGES_FOR_SURAH[surahNumber - 1]

    fun getSurahAyahsCount(surahNumber: Int) = QuranConstants.SURAH_AYAHS_NUMBER[surahNumber - 1]

}