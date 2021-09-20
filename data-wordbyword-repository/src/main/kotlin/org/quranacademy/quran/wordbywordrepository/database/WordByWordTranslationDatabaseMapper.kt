package org.quranacademy.quran.wordbywordrepository.database

import org.quranacademy.quran.data.database.models.WordByWordTranslationModel
import org.quranacademy.quran.domain.models.WordByWordTranslation
import javax.inject.Inject

class WordByWordTranslationDatabaseMapper @Inject constructor() {

    fun mapFromDatabase(models: List<WordByWordTranslationModel>) = models.map { mapFromDatabase(it) }

    fun mapFromDatabase(model: WordByWordTranslationModel): WordByWordTranslation {
        val isTranslationAvailable = if (model.isDownloaded && model.localLastUpdateTime == null) {
            true
        } else if (model.localLastUpdateTime == null) {
            false
        } else {
            model.remoteLastUpdateTime.isAfter(model.localLastUpdateTime)
        }
        return WordByWordTranslation(
                id = model.id,
                languageCode = model.languageCode,
                name = model.name,
                fileName = model.fileName,
                fileUrl = model.fileUrl,
                isDownloaded = model.isDownloaded,
                isUpdateAvailable = isTranslationAvailable
        )
    }

}