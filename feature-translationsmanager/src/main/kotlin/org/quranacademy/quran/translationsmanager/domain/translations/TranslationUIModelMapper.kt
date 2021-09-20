package org.quranacademy.quran.translationsmanager.domain.translations

import org.quranacademy.quran.domain.models.Language
import org.quranacademy.quran.domain.models.Translation
import javax.inject.Inject

class TranslationUIModelMapper @Inject constructor() {

    fun mapToUIModel(
            models: List<Translation>,
            languages: List<Language>,
            downloadingTranslationCodes: List<String>
    ): List<TranslationUIModel> {
        val languagesMap = languages.associateBy { it.code }
        return models.map {
            val isDownloading = downloadingTranslationCodes.contains(it.code)
            mapToUIModel(it, languagesMap, isDownloading)
        }
    }

    fun mapToUIModel(
            model: Translation,
            languages: Map<String, Language>,
            isDownloading: Boolean
    ): TranslationUIModel {
        return TranslationUIModel(
                code = model.code,
                languageCode = model.languageCode,
                languageName = languages[model.languageCode]!!.name,
                name = model.name,
                fileName = model.fileName,
                fileUrl = model.fileUrl,
                fileSize = model.fileSize,
                isTafseer = model.isTafseer,
                isDownloaded = model.isDownloaded,
                isEnabled = model.isEnabled,
                isUpdateAvailable = model.isUpdateAvailable,
                isDownloading = isDownloading
        )
    }

    fun mapFromUIModel(model: TranslationUIModel): Translation {
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
                isUpdateAvailable = model.isUpdateAvailable
        )
    }

}