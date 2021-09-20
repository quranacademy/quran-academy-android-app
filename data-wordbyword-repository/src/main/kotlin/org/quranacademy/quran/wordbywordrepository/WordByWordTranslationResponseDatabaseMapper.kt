package org.quranacademy.quran.wordbywordrepository

import org.quranacademy.quran.data.database.models.WordByWordTranslationModel
import org.quranacademy.quran.data.network.responses.WordByWordTranslationResponse
import javax.inject.Inject

class WordByWordTranslationResponseDatabaseMapper @Inject constructor() {

    fun map(responses: List<WordByWordTranslationResponse>): List<WordByWordTranslationModel> {
        return responses.map {
            WordByWordTranslationModel(
                    name = it.languageName,
                    languageCode = it.languageCode,
                    fileUrl = it.fileUrl,
                    fileName = "quran-wbw-translation-${it.languageCode}.db",
                    isDownloaded = false,
                    remoteLastUpdateTime = it.lastUpdateTime
            )
        }
    }

}
