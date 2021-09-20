package org.quranacademy.quran.languagesrepository.database

import org.quranacademy.quran.data.database.models.LanguageModel
import org.quranacademy.quran.domain.models.Language
import javax.inject.Inject

class LanguageDatabaseMapper @Inject constructor() {

    fun mapFromDatabase(entities: List<LanguageModel>): List<Language> {
        return entities.map { mapFromDatabase(it) }
    }

    fun mapFromDatabase(entity: LanguageModel): Language {
        return Language(
                code = entity.code,
                name = entity.name,
                englishName = entity.englishName,
                isRtl = entity.isRtl,
                isDownloaded = entity.isDownloaded
        )
    }

}

