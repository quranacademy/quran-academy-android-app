package org.quranacademy.quran.surahsrepository

import org.quranacademy.quran.data.database.models.SurahNameTranslationModel
import org.quranacademy.quran.data.network.responses.SurahResponse
import org.quranacademy.quran.domain.models.Language
import javax.inject.Inject

class SurahNameTranslationResponseDatabaseMapper @Inject constructor() {

    fun map(responses: List<SurahResponse>, language: Language): List<SurahNameTranslationModel> {
        return responses.map {
            SurahNameTranslationModel(
                    surahNumber = it.surahNumber,
                    language = language.code,
                    transliteratedName = it.name.transliteratedName,
                    translatedName = it.name.translatedName
            )
        }
    }

}