package org.quranacademy.quran.languagesrepository

import org.quranacademy.quran.data.RecitationsListDownloader
import org.quranacademy.quran.data.SurahsListDownloader
import org.quranacademy.quran.data.database.daos.LanguagesDao
import org.quranacademy.quran.data.translations.TranslationsListDownloader
import org.quranacademy.quran.data.translations.WordByWordTranslationsListDownloader
import org.quranacademy.quran.domain.models.Language
import javax.inject.Inject

class LanguageDownloader @Inject constructor(
        private val languagesDao: LanguagesDao,
        private val surahsListDownloader: SurahsListDownloader,
        private val recitationsListDownloader: RecitationsListDownloader,
        private val translationsListDownloader: TranslationsListDownloader,
        private val wordByWordTranslationsListDownloader: WordByWordTranslationsListDownloader
) {

    suspend fun downloadLanguageData(language: Language) {
        surahsListDownloader.downloadSurahsList(language)
        recitationsListDownloader.downloadRecitationsList(language)
        translationsListDownloader.updateTranslationsList(language)
        wordByWordTranslationsListDownloader.updateTranslationsList(language)
        languagesDao.markLanguageAsDownloaded(language.code, true)
    }

    suspend fun updateLanguageData(language: Language) {
        recitationsListDownloader.downloadRecitationsList(language)
        translationsListDownloader.updateTranslationsList(language)
        wordByWordTranslationsListDownloader.updateTranslationsList(language)
    }

    suspend fun deleteLanguage(language: Language) {
        surahsListDownloader.deleteSurahsListForLanguage(language)
        languagesDao.markLanguageAsDownloaded(language.code, false)
    }

}