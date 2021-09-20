package org.quranacademy.quran.surahsrepository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.quranacademy.quran.data.AyahsDataSource
import org.quranacademy.quran.data.database.daos.SurahsDao
import org.quranacademy.quran.domain.models.Surah
import org.quranacademy.quran.domain.models.SurahDetails
import org.quranacademy.quran.domain.repositories.SurahsRepository
import javax.inject.Inject

class SurahsRepositoryImpl @Inject constructor(
        private val ayahsDataSource: AyahsDataSource,
        private val surahsDao: SurahsDao,
        private val surahDatabaseMapper: SurahDatabaseMapper,
        private val surahDetailsMapper: SurahDetailsMapper
) : SurahsRepository {

    override suspend fun getSurahsList(): List<Surah> = withContext(Dispatchers.IO) {
        surahDatabaseMapper.map(surahsDao.getSurahs())
    }

    override suspend fun getSurahDetails(surahNumber: Int): SurahDetails = withContext(Dispatchers.IO) {
        val surahModel = surahsDao.getSurahByNumber(surahNumber)
        val surah = surahDatabaseMapper.map(surahModel)
        val ayahs = ayahsDataSource.getSurahAyahs(surahNumber)
        surahDetailsMapper.map(surah, ayahs)
    }

}