package org.quranacademy.quran.translationsrepository.database

import org.quranacademy.quran.data.database.models.TranslationModel
import org.quranacademy.quran.data.network.responses.TranslationResponse
import javax.inject.Inject

class TranslationResponseDatabaseMapper @Inject constructor() {

    fun map(responses: List<TranslationResponse>): List<TranslationModel> {
        return responses.map {
            TranslationModel(
                    code = it.code,
                    name = it.name,
                    languageCode = it.languageCode,
                    fileUrl = it.fileUrl,
                    fileSize = it.fileSize,
                    fileName = "quran-translation-${it.code}.db",
                    isTafseer = it.isTafseer,
                    remoteLastUpdateTime = it.lastUpdateTime
            )
        }
    }

}
