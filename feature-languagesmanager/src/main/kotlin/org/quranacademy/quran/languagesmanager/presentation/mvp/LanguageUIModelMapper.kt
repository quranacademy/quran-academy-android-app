package org.quranacademy.quran.languagesmanager.presentation.mvp

import org.quranacademy.quran.domain.models.Language
import javax.inject.Inject

class LanguageUIModelMapper @Inject constructor() {

    fun mapToViewModel(
            models: List<Language>,
            currentLanguageCode: String
    ) = models.map { mapToViewModel(it, currentLanguageCode) }

    fun mapToViewModel(
            model: Language,
            currentLanguageCode: String
    ): LanguageUIModel {
        return LanguageUIModel(
                code = model.code,
                name = model.name,
                englishName = model.englishName,
                isRtl = model.isRtl,
                isDownloaded = model.isDownloaded,
                isEnabled = model.code == currentLanguageCode

        )
    }

    fun mapFromViewModel(model: LanguageUIModel): Language {
        return Language(
                code = model.code,
                name = model.name,
                englishName = model.englishName,
                isRtl = model.isRtl,
                isDownloaded = model.isDownloaded
        )
    }

}