package org.quranacademy.quran.domain.repositories

import org.quranacademy.quran.domain.models.Surah
import org.quranacademy.quran.domain.models.SurahDetails

interface SurahsRepository {

    suspend fun getSurahsList(): List<Surah>

    suspend fun getSurahDetails(surahNumber: Int): SurahDetails

}