package org.quranacademy.quran.ayahsrepository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.quranacademy.quran.data.database.daos.AyahTranslationsDao
import org.quranacademy.quran.data.database.daos.AyahsBookmarksDao
import org.quranacademy.quran.data.database.daos.AyahsDao
import org.quranacademy.quran.data.database.daos.SurahsNameTranslationsDao
import org.quranacademy.quran.data.translations.WordByWordDataSource
import org.quranacademy.quran.domain.models.AyahDetails
import org.quranacademy.quran.domain.models.AyahId
import org.quranacademy.quran.domain.models.AyahTranslation
import org.quranacademy.quran.domain.models.AyahWordItem
import org.quranacademy.quran.domain.repositories.AyahsRepository
import timber.log.Timber
import javax.inject.Inject

class AyahsRepositoryImpl @Inject constructor(
        private val ayahsDao: AyahsDao,
        private val ayahTranslationsDao: AyahTranslationsDao,
        private val ayahsBookmarksDao: AyahsBookmarksDao,
        private val surahsNameTranslationsDao: SurahsNameTranslationsDao,
        private val wordByWordDataSource: WordByWordDataSource,
        private val ayahTranslationMapper: AyahTranslationDatabaseMapper
) : AyahsRepository {

    override suspend fun getAyahDetails(
            ayahId: AyahId,
            loadTranslations: Boolean,
            loadAllTranslations: Boolean
    ): AyahDetails = withContext(Dispatchers.IO) {
        val surahNumber = ayahId.surahNumber
        val ayahNumber = ayahId.ayahNumber
        Timber.i("Получение деталей аята. Сура - $surahNumber, аят - $ayahNumber")
        val ayah = ayahsDao.getAyah(surahNumber, ayahNumber)
        val ayahSurahName = surahsNameTranslationsDao.getSurahNameByNumber(surahNumber)

        val translations: List<AyahTranslation>
        val ayahWordByWord: List<AyahWordItem>?
        if (loadTranslations) {
            val translationsMap = if (loadAllTranslations) {
                ayahTranslationsDao.getAllAyahTranslations(ayah.surahNumber, ayah.ayahNumber)
            } else {
                ayahTranslationsDao.getTranslationsForShowingInDialog(ayah.surahNumber, ayah.ayahNumber)
            }
            translations = translationsMap.map { ayahTranslationMapper.map(it.value, it.key) }
            ayahWordByWord = if (wordByWordDataSource.isWordByWordEnabled()) {
                wordByWordDataSource.getAyahWordByWord(surahNumber, ayahNumber)
            } else null
        } else {
            translations = emptyList()
            ayahWordByWord = null
        }

        val isAyahBookmarked = ayahsBookmarksDao.isAyahBookmarked(surahNumber, ayahNumber)
        AyahDetails(
                surahNumber = ayah.surahNumber,
                ayahNumber = ayah.ayahNumber,
                arabicText = ayah.arabicText,
                arabicTextTajweed = ayah.arabicTextTajweed,
                surahName = ayahSurahName.transliteratedName,
                translations = translations,
                words = ayahWordByWord,
                isBookmarked = isAyahBookmarked
        )
    }

}