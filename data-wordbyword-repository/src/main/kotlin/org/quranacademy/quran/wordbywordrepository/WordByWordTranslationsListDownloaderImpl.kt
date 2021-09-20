package org.quranacademy.quran.wordbywordrepository

import org.quranacademy.quran.data.translations.WordByWordTranslationsListDownloader
import org.quranacademy.quran.domain.models.Language
import javax.inject.Inject

class WordByWordTranslationsListDownloaderImpl @Inject constructor(
        private val wordByWordTranslationsDataSource: WordByWordTranslationsDataSource
) : WordByWordTranslationsListDownloader {

    override suspend fun updateTranslationsList(language: Language?) {
        wordByWordTranslationsDataSource.getTranslations(language, true)
    }

}