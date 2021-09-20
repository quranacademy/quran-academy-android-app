package org.quranacademy.quran.languagesrepository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.quranacademy.quran.data.prefs.AppPreferences
import org.quranacademy.quran.domain.models.Language
import org.quranacademy.quran.domain.models.LanguagesWrapper
import org.quranacademy.quran.domain.repositories.LanguagesRepository
import javax.inject.Inject

class LanguagesRepositoryImpl @Inject constructor(
        private val appPreferences: AppPreferences,
        private val languagesDataSource: LanguagesDataSource,
        private val languageDownloader: LanguageDownloader
) : LanguagesRepository {

    private val languageUpdates = BroadcastChannel<Language>(1)

    init {
        GlobalScope.launch {
            appPreferences.getLanguageUpdates()
                    .map { languagesDataSource.getLanguage(it) }
                    .collect { languageUpdates.send(it) }
        }
    }

    override suspend fun setAppLanguage(language: Language) = withContext(Dispatchers.IO) {
        appPreferences.setAppLanguage(language.code)
        languageDownloader.updateLanguageData(language)
    }

    override fun getLanguageChanges(): ReceiveChannel<Language> {
        return languageUpdates.openSubscription()
    }

    override suspend fun getLanguages(forceLoad: Boolean): LanguagesWrapper = withContext(Dispatchers.IO) {
        val languages = languagesDataSource.getLanguagesList(forceLoad)
                .sortedBy { language -> language.name }
        LanguagesWrapper(languages, appPreferences.getAppLanguage())
    }

    override suspend fun downloadLanguageData(language: Language) = withContext(Dispatchers.IO) {
        languageDownloader.downloadLanguageData(language)
        appPreferences.setAppLanguage(language.code)
    }

    override suspend fun removeLanguage(language: Language) = withContext(Dispatchers.IO) {
        languageDownloader.deleteLanguage(language)
    }

}