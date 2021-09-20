package org.quranacademy.quran.presentation

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.quranacademy.quran.data.prefs.AppPreferences
import org.quranacademy.quran.domain.models.Language
import org.quranacademy.quran.languagesrepository.LanguagesCacheIsEmptyException
import org.quranacademy.quran.languagesrepository.LanguagesDataSource
import org.quranacademy.quran.presentation.ui.appearance.LanguageManager
import org.quranacademy.quran.presentation.ui.languagesystem.Philology
import javax.inject.Inject

class LanguageManagerImpl @Inject constructor(
        private val appPreferences: AppPreferences,
        private val languagesDataSource: LanguagesDataSource
) : LanguageManager {

    private val languageUpdates = BroadcastChannel<String>(1)
    private var currentLanguage: Language

    init {
        GlobalScope.launch {
            appPreferences.getLanguageUpdates()
                    .collect { languageCode ->
                        currentLanguage = languagesDataSource.getLanguage(appPreferences.getAppLanguage())
                        Philology.setLanguage(languageCode)
                        languageUpdates.send(languageCode)
                    }
        }

        currentLanguage = try {
            languagesDataSource.getLanguage(appPreferences.getAppLanguage())
        } catch (error: LanguagesCacheIsEmptyException) {
            getDefaultLanguage()
        }
    }

    override fun setAppLanguage(languageCode: String) {
        appPreferences.setAppLanguage(languageCode)
    }

    override fun getCurrentAppLanguage(): Language = currentLanguage

    override fun getLanguageUpdates(): Flow<String> = languageUpdates.asFlow()

    private fun getDefaultLanguage() = Language(
            code = "en",
            name = "English",
            englishName = "English",
            isRtl = false,
            isDownloaded = false
    )

}