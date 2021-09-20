package org.quranacademy.quran.surahsrepository

import org.quranacademy.quran.QuranConstants
import org.quranacademy.quran.data.database.daos.SurahsNameTranslationsDao
import org.quranacademy.quran.data.database.models.SurahModel
import org.quranacademy.quran.data.database.models.SurahNameTranslationModel
import org.quranacademy.quran.domain.models.Surah
import javax.inject.Inject

class SurahDatabaseMapper @Inject constructor(
        private val surahsNameTranslationsDao: SurahsNameTranslationsDao
) {

    fun map(models: List<SurahModel>): List<Surah> {
        val nameTranslations = surahsNameTranslationsDao.getAllSurahNames()
        return models.mapIndexed { index, surahModel ->
            val translation = nameTranslations[index]
            return@mapIndexed map(surahModel, translation)
        }
    }

    fun map(model: SurahModel): Surah {
        val translation = surahsNameTranslationsDao.getSurahNameByNumber(model.surahNumber)
        return map(model, translation)
    }

    private fun map(surahModel: SurahModel, translation: SurahNameTranslationModel): Surah {
        return Surah(
                id = surahModel.id,
                surahNumber = surahModel.surahNumber,
                pageNumber = QuranConstants.PAGES_FOR_SURAH[surahModel.surahNumber - 1],
                bismillahPre = surahModel.bismillahPre,
                // revelationPlace = entity.revelationPlace,
                arabicName = surahModel.arabicName,
                transliteratedName = translation.transliteratedName,
                translatedName = translation.translatedName,
                ayahsCount = surahModel.ayahsCount,
                juzNumber = surahModel.juzNumber
        )
    }

}