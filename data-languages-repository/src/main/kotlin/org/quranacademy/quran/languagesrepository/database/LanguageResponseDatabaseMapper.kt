package org.quranacademy.quran.languagesrepository.database

import org.quranacademy.quran.data.database.models.LanguageModel
import org.quranacademy.quran.data.network.responses.LanguageResponse
import javax.inject.Inject

class LanguageResponseDatabaseMapper @Inject constructor() {

    fun map(models: List<LanguageResponse>) = models.map {
        LanguageModel(
                code = it.code,
                name = it.name,
                englishName = it.englishName,
                isRtl = it.isRtl,
                isDownloaded = false
        )
    }

}

