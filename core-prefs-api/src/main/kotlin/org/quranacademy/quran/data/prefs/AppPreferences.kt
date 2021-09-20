package org.quranacademy.quran.data.prefs

import kotlinx.coroutines.flow.Flow
import org.joda.time.DateTime
import org.quranacademy.quran.domain.models.MushafPageType
import org.quranacademy.quran.domain.models.ReadSettings

interface AppPreferences {

    companion object {
        const val APP_LANGUAGE_DEFAULT = "en"
        const val IS_INITIAL_SETUP_COMPLETED_DEFAULT = false
        const val CURRENT_QURAN_TEXT_VERSION_DEFAULT = -1
        val APP_DATA_PATH_DEFAULT = null

        const val IS_TRANSLATIONS_COPYRIGHT_DIALOG_SHOWED_DEFAULT = false

        const val LANGUAGES_LAST_UPDATE_DEFAULT = -1L
        const val TRANSLATIONS_LAST_UPDATE_DEFAULT = -1L
        const val WORD_BY_WORD_TRANSLATIONS_LAST_UPDATE_DEFAULT = -1L

        const val WORD_BY_WORD_ENABLED_DEFAULT = true
        val CURRENT_WORD_BY_WORD_TRANSLATION_DEFAULT = null

        const val TRANSLATIONS_UPDATES_CHECKING_ENABLED_DEFAULT = true
        const val TRANSLATIONS_UPDATES_NOTIFICATION_SHOWING_TIME_DEFAULT = -1L
        const val RECITATIONS_LIST_UPDATE_TIME_DEFAULT = -1L

        val QURAN_ARABIC_FONT_DEFAULT = null
        const val OPEN_LAST_READING_PLACE_ON_START_ENABLED_DEFAULT = false
        const val READING_HISTORY_SIZE_DEFAULT = 3
        const val TAJWEED_ENABLED_DEFAULT = false
        const val SURAHS_LIST_SCROLL_POSITION_DEFAULT = 0

        const val PLAYER_AUTO_SCROLL_ENABLED_DEFAULT = true
        const val WORDS_HIGHLIGHTING_ENABLED_DEFAULT = false

        const val IS_MUSHAF_MODE_DEFAULT = false
        val MUSHAF_PAGE_TYPE_DEFAULT = null
        const val LAST_READ_SURAH_DEFAULT = 1
        const val LAST_READ_AYAH_DEFAULT = 1
        const val CURRENT_RECITATION_ID_DEFAULT = -1L

        const val SUGGEST_DOWNLOAD_IMAGES_BUNDLE_DEFAULT = true

        val TRANSLATIONS_FOR_SEARCH_DEFAULT = null
    }

    fun getAppLanguage(): String
    fun setAppLanguage(language: String)

    fun getAppDataFilePath(): String?
    fun setAppDataFilePath(path: String)

    fun getLanguageUpdates(): Flow<String>

    fun getCurrentQuranTextVersion(): Int
    fun setCurrentQuranTextVersion(version: Int)

    fun isInitialSetupCompleted(): Boolean
    fun setIsInitialSetupCompleted(isCompleted: Boolean)

    fun getLanguagesLastUpdateTime(): DateTime?
    fun setLanguagesLastUpdateTime(time: DateTime)

    fun getTranslationsLastUpdateTime(): DateTime?
    fun setTranslationsLastUpdateTime(time: DateTime)

    fun getWordByWordTranslationsLastUpdateTime(): DateTime?
    fun setWordByWordTranslationsLastUpdateTime(time: DateTime)

    fun isWordByWordEnabled(): Boolean
    fun setWordByWordEnabled(isEnabled: Boolean)

    fun isPlayerAutoScrollEnabled(): Boolean
    fun setPlayerAutoScrollEnabled(isEnabled: Boolean)

    fun isWordsHighlightingEnabled(): Boolean
    fun setWordsHighlightingEnabled(isEnabled: Boolean)

    //Wbw = Word by Word
    fun getWbwEnablingUpdates(): Flow<Boolean>

    fun getCurrentWbwTranslation(): String?
    fun setCurrentWbwTranslation(translationCode: String?)

    fun setTranslationUpdatesCheckingEnabled(isEnabled: Boolean)
    fun isTranslationUpdatesCheckingEnabled(): Boolean

    fun setTranslationUpdatesNotificationShowingTime(time: DateTime)
    fun getTranslationUpdatesNotificationShowingTime(): DateTime?

    fun getReadSettings(): ReadSettings
    fun saveReadSettings(settings: ReadSettings)

    fun getReadingHistorySize(): Int
    fun setReadingHistorySize(size: Int)

    fun suggestDownloadImagesBundle(): Boolean
    fun setSuggestDownloadImagesBundle(isEnabled: Boolean)

    fun getSurahsListLastPosition(): Int
    fun setSurahsListLastPosition(position: Int)

    fun setRecitationsListUpdateTime(time: DateTime)
    fun getRecitationsListUpdateTime(): DateTime?

    fun setCurrentRecitationId(recitationId: Long)
    fun getCurrentRecitationId(): Long

    fun setTranslationsCopyrightDialogShowed(isShowed: Boolean)
    fun isTranslationsCopyrightDialogShowed(): Boolean

    fun setTranslationsForSearch(translations: List<String>)
    fun getTranslationsForSearch(): List<String>?

    fun setMushafPageType(pageType: MushafPageType)
    fun getMushafType(): MushafPageType

}