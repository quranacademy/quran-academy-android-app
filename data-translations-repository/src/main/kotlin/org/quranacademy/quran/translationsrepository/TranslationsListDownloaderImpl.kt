package org.quranacademy.quran.translationsrepository

import org.quranacademy.quran.data.translations.TranslationsListDownloader
import org.quranacademy.quran.domain.models.Language
import javax.inject.Inject

class TranslationsListDownloaderImpl @Inject constructor(
        private val translationsDataSource: TranslationsDataSource
) : TranslationsListDownloader {

    override suspend fun updateTranslationsList(language: Language?) {
        translationsDataSource.getTranslationsList(language, true)
    }

}