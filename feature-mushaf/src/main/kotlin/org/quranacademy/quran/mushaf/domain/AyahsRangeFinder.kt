package org.quranacademy.quran.mushaf.domain

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.quranacademy.quran.QuranConstants
import org.quranacademy.quran.domain.models.AyahId
import org.quranacademy.quran.domain.repositories.SurahsRepository
import javax.inject.Inject

class AyahsRangeFinder @Inject constructor(
        private val surahsRepository: SurahsRepository
) {

    suspend fun getLastPageAyah(pageNumber: Int): AyahId = withContext(Dispatchers.Unconfined) {
        val isLastPage = pageNumber == QuranConstants.QURAN_PAGES_COUNT
        if (isLastPage) {
            val lastSurahNumber = QuranConstants.SURAHS_COUNT
            val lastSurahAyah = QuranConstants.SURAH_AYAHS_NUMBER[lastSurahNumber - 1]
            AyahId(lastSurahNumber, lastSurahAyah)
        } else {
            val nextPageNumber = pageNumber + 1
            val nextPageSurahNumber = QuranConstants.SURAH_FOR_PAGE[nextPageNumber - 1]
            val nextPageAyahNumber = QuranConstants.AYAH_FOR_PAGE[nextPageNumber - 1]

            val isNewSurahStartsOnNextPage = nextPageAyahNumber == 1
            if (isNewSurahStartsOnNextPage) {
                val currentPageLastSurah = nextPageSurahNumber - 1
                val currentPageLastSurahAyahsCount = QuranConstants.SURAH_AYAHS_NUMBER[currentPageLastSurah - 1]
                AyahId(currentPageLastSurah, currentPageLastSurahAyahsCount)
            } else {
                val currentPageLastAyah = nextPageAyahNumber - 1
                AyahId(nextPageSurahNumber, currentPageLastAyah)
            }
        }
    }

    suspend fun getAyahsRange(start: AyahId, end: AyahId): List<AyahId> = withContext(Dispatchers.Unconfined) {
        surahsRepository.getSurahsList()
                .filter { start.surahNumber <= it.surahNumber && it.surahNumber <= end.surahNumber }
                .map { surah ->
                    val surahNumber = surah.surahNumber
                    val ayahsCount = surah.ayahsCount
                    val surahAyahs = when {
                        start.surahNumber == end.surahNumber -> (start.ayahNumber..end.ayahNumber)
                        surahNumber == start.surahNumber -> (start.ayahNumber..ayahsCount)
                        surahNumber == end.surahNumber -> (1..end.ayahNumber)
                        else -> (1..ayahsCount)
                    }
                    return@map surahAyahs.map { AyahId(surahNumber, it) }.toTypedArray()
                }
                .toTypedArray()
                .flatten() //merge list of arrays to single list
    }

}