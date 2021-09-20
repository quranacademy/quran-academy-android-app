package org.quranacademy.quran.translationsrepository.database

import org.quranacademy.quran.data.database.models.TranslationModel
import org.quranacademy.quran.domain.models.Translation
import javax.inject.Inject

class TranslationDatabaseMapper @Inject constructor() {

    fun mapFromDatabase(
            translations: List<TranslationModel>
    ): List<Translation> {
        return translations.map { mapFromDatabase(it) }
    }

    fun mapFromDatabase(model: TranslationModel): Translation {
        val isTranslationAvailable = if (model.isDownloaded && model.localLastUpdateTime == null) {
            true
        } else if (model.localLastUpdateTime == null) {
            false
        } else {
            model.remoteLastUpdateTime.isAfter(model.localLastUpdateTime)
        }
        return Translation(
                code = model.code,
                languageCode = model.languageCode,
                name = model.name,
                fileName = model.fileName,
                fileUrl = model.fileUrl,
                fileSize = model.fileSize,
                isTafseer = model.isTafseer,
                isDownloaded = model.isDownloaded,
                isEnabled = model.isEnabled,
                isUpdateAvailable = isTranslationAvailable
        )
    }

}

