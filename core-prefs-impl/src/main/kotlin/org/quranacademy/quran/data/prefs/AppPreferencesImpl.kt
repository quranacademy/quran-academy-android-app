package org.quranacademy.quran.data.prefs

import android.content.Context
import com.google.gson.FieldNamingPolicy
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.Flow
import org.joda.time.DateTime
import org.quranacademy.quran.domain.models.MushafPageType
import org.quranacademy.quran.domain.models.ReadSettings
import javax.inject.Inject

class AppPreferencesImpl @Inject constructor(
        context: Context
) : BasePreferences(context), AppPreferences {

    private val gson = GsonBuilder()
            .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
            .create()

    override fun getAppLanguage(): String {
        return getString(APP_LANGUAGE)!!
    }

    override fun setAppLanguage(language: String) {
        putString(APP_LANGUAGE, language)
    }

    override fun getAppDataFilePath(): String? {
        return getString(APP_DATA_PATH)
    }

    override fun setAppDataFilePath(path: String) {
        putString(APP_DATA_PATH, path)
    }

    override fun getCurrentQuranTextVersion(): Int {
        return getInt(CURRENT_QURAN_TEXT_VERSION)
    }

    override fun setCurrentQuranTextVersion(version: Int) {
        putInt(CURRENT_QURAN_TEXT_VERSION, version)
    }

    override fun isInitialSetupCompleted(): Boolean {
        return getBoolean(IS_INITIAL_SETUP_COMPLETED)
    }

    override fun setIsInitialSetupCompleted(isCompleted: Boolean) {
        putBoolean(IS_INITIAL_SETUP_COMPLETED, isCompleted)
    }

    override fun getLanguagesLastUpdateTime(): DateTime? {
        val time = getLong(LANGUAGES_LAST_UPDATE)
        return if (time != -1L) DateTime(time) else null
    }

    override fun setLanguagesLastUpdateTime(time: DateTime) = putLong(LANGUAGES_LAST_UPDATE, time.millis)

    override fun getTranslationsLastUpdateTime(): DateTime? {
        val time = getLong(TRANSLATIONS_LAST_UPDATE)
        return if (time != -1L) DateTime(time) else null
    }

    override fun setTranslationsLastUpdateTime(time: DateTime) = putLong(TRANSLATIONS_LAST_UPDATE, time.millis)

    override fun getWordByWordTranslationsLastUpdateTime(): DateTime? {
        val time = getLong(WORD_BY_WORD_TRANSLATIONS_LAST_UPDATE)
        return if (time != -1L) DateTime(time) else null
    }

    override fun setWordByWordTranslationsLastUpdateTime(time: DateTime) {
        putLong(WORD_BY_WORD_TRANSLATIONS_LAST_UPDATE, time.millis)
    }

    override fun isWordByWordEnabled(): Boolean {
        return getBoolean(WORD_BY_WORD_ENABLED)
    }

    override fun setWordByWordEnabled(isEnabled: Boolean) {
        putBoolean(WORD_BY_WORD_ENABLED, isEnabled)
    }

    override fun getWbwEnablingUpdates(): Flow<Boolean> {
        return preferenceUpdatesObserver.observeBoolean(WORD_BY_WORD_ENABLED)
    }

    override fun getCurrentWbwTranslation(): String? {
        return getString(CURRENT_WORD_BY_WORD_TRANSLATION)
    }

    override fun setCurrentWbwTranslation(translationCode: String?) {
        putString(CURRENT_WORD_BY_WORD_TRANSLATION, translationCode)
    }

    override fun isPlayerAutoScrollEnabled(): Boolean {
        return getBoolean(PLAYER_AUTO_SCROLL_ENABLED)
    }

    override fun setPlayerAutoScrollEnabled(isEnabled: Boolean) {
        putBoolean(PLAYER_AUTO_SCROLL_ENABLED, isEnabled)
    }

    override fun isWordsHighlightingEnabled(): Boolean {
        return getBoolean(WORDS_HIGHLIGHTING_ENABLED)
    }

    override fun setWordsHighlightingEnabled(isEnabled: Boolean) {
        putBoolean(WORDS_HIGHLIGHTING_ENABLED, isEnabled)
    }

    override fun setTranslationUpdatesCheckingEnabled(isEnabled: Boolean) {
        putBoolean(TRANSLATIONS_UPDATES_CHECKING_ENABLED, isEnabled)
    }

    override fun isTranslationUpdatesCheckingEnabled(): Boolean {
        return getBoolean(TRANSLATIONS_UPDATES_CHECKING_ENABLED)
    }

    override fun setTranslationUpdatesNotificationShowingTime(time: DateTime) {
        putLong(TRANSLATIONS_UPDATES_NOTIFICATION_SHOWING_TIME, time.millis)
    }

    override fun getTranslationUpdatesNotificationShowingTime(): DateTime? {
        val time = getLong(TRANSLATIONS_UPDATES_NOTIFICATION_SHOWING_TIME)
        return if (time != AppPreferences.TRANSLATIONS_UPDATES_NOTIFICATION_SHOWING_TIME_DEFAULT) DateTime(time) else null
    }

    override fun setRecitationsListUpdateTime(time: DateTime) {
        putLong(RECITATIONS_LIST_UPDATE_TIME, time.millis)
    }

    override fun getRecitationsListUpdateTime(): DateTime? {
        val time = getLong(RECITATIONS_LIST_UPDATE_TIME)
        return if (time != AppPreferences.RECITATIONS_LIST_UPDATE_TIME_DEFAULT) DateTime(time) else null
    }

    override fun setCurrentRecitationId(recitationId: Long) {
        putLong(CURRENT_RECITATION_ID, recitationId)
    }

    override fun getCurrentRecitationId(): Long {
        return getLong(CURRENT_RECITATION_ID)
    }

    override fun getReadSettings(): ReadSettings {
        return ReadSettings(
                isOpenLastReadingPlaceEnabled = getBoolean(OPEN_LAST_READING_PLACE_ON_START_ENABLED),
                isMushafMode = getBoolean(IS_MUSHAF_MODE),
                lastReadSurah = getInt(LAST_READ_SURAH),
                lastReadAyah = getInt(LAST_READ_AYAH),
                arabicFont = getString(QURAN_ARABIC_FONT)
        )
    }

    override fun saveReadSettings(settings: ReadSettings) {
        putBoolean(IS_MUSHAF_MODE, settings.isMushafMode)
        putInt(LAST_READ_SURAH, settings.lastReadSurah)
        putInt(LAST_READ_AYAH, settings.lastReadAyah)
        putString(QURAN_ARABIC_FONT, settings.arabicFont)
    }

    override fun getReadingHistorySize(): Int {
        return getInt(READING_HISTORY_SIZE)
    }

    override fun setReadingHistorySize(size: Int) {
        putInt(READING_HISTORY_SIZE, size)
    }

    override fun suggestDownloadImagesBundle(): Boolean {
        return getBoolean(SUGGEST_DOWNLOAD_IMAGES_BUNDLE)
    }

    override fun setSuggestDownloadImagesBundle(isEnabled: Boolean) {
        putBoolean(SUGGEST_DOWNLOAD_IMAGES_BUNDLE, isEnabled)
    }

    override fun getSurahsListLastPosition(): Int {
        return getInt(SURAHS_LIST_SCROLL_POSITION)
    }

    override fun setSurahsListLastPosition(position: Int) {
        putInt(SURAHS_LIST_SCROLL_POSITION, position)
    }

    override fun getLanguageUpdates(): Flow<String> {
        return preferenceUpdatesObserver.observeString(APP_LANGUAGE)
    }

    override fun setTranslationsCopyrightDialogShowed(isShowed: Boolean) {
        putBoolean(IS_TRANSLATIONS_COPYRIGHT_DIALOG_SHOWED, isShowed)
    }

    override fun isTranslationsCopyrightDialogShowed(): Boolean {
        return getBoolean(IS_TRANSLATIONS_COPYRIGHT_DIALOG_SHOWED)
    }

    override fun setTranslationsForSearch(translations: List<String>) {
        putString(TRANSLATIONS_FOR_SEARCH, gson.toJson(translations))
    }

    override fun getTranslationsForSearch(): List<String>? {
        return getString(TRANSLATIONS_FOR_SEARCH)?.let { translationsListJson ->
            val stringsListType = object : TypeToken<ArrayList<String>>() {}.type
            return gson.fromJson(translationsListJson, stringsListType)
        }

    }

    override fun setMushafPageType(pageType: MushafPageType) {
        putString(MUSHAF_PAGE_TYPE, pageType.code)
    }

    override fun getMushafType(): MushafPageType {
        val pageTypeCode = getString(MUSHAF_PAGE_TYPE) ?: MushafPageType.MADANI.code
        return MushafPageType.findFontByCode(pageTypeCode) ?: MushafPageType.MADANI
    }

    companion object {
        const val APP_LANGUAGE = "app_language"
        const val APP_DATA_PATH = "app_data_path"
        const val CURRENT_QURAN_TEXT_VERSION = "current_quran_text_version"
        const val CURRENT_RECITATION_ID = "current_recitation_id"
        const val CURRENT_WORD_BY_WORD_TRANSLATION = "current_word_by_word_translation"
        const val IS_INITIAL_SETUP_COMPLETED = "initial_setup_completed"
        const val IS_MUSHAF_MODE = "is_mushaf_mode"
        const val LANGUAGES_LAST_UPDATE = "languages_last_update"
        const val LAST_READ_SURAH = "last_read_surah"
        const val LAST_READ_AYAH = "last_read_ayah"
        const val OPEN_LAST_READING_PLACE_ON_START_ENABLED = "open_last_reading_place_on_start_enabled"
        const val READING_HISTORY_SIZE = "reading_history_size"
        const val QURAN_ARABIC_FONT = "quran_arabic_font"
        const val RECITATIONS_LIST_UPDATE_TIME = "recitations_list_update_time"
        const val SUGGEST_DOWNLOAD_IMAGES_BUNDLE = "suggest_download_images_bundle"
        const val IS_TRANSLATIONS_COPYRIGHT_DIALOG_SHOWED = "is_translations_copyright_dialog_showed"
        const val SURAHS_LIST_SCROLL_POSITION = "surahs_list_scroll_position"
        const val TAJWEED_ENABLED = "tajweed_enabled"
        const val PLAYER_AUTO_SCROLL_ENABLED = "player_auto_scroll_enabled"
        const val WORDS_HIGHLIGHTING_ENABLED = "words_highlighting_enabled"
        const val TRANSLATIONS_UPDATES_CHECKING_ENABLED = "translations_update_checking_enabled"
        const val TRANSLATIONS_UPDATES_NOTIFICATION_SHOWING_TIME = "translations_updates_notification_showing_time"
        const val TRANSLATIONS_LAST_UPDATE = "translations_last_update"
        const val WORD_BY_WORD_ENABLED = "word_by_word_enabled"
        const val WORD_BY_WORD_TRANSLATIONS_LAST_UPDATE = "word_by_word_translations_last_update"
        const val TRANSLATIONS_FOR_SEARCH = "translations_for_search"
        const val MUSHAF_PAGE_TYPE = "mushaf_page_type"

        init {
            DEFAULT_VALUES[APP_LANGUAGE] = AppPreferences.APP_LANGUAGE_DEFAULT
            DEFAULT_VALUES[APP_DATA_PATH] = AppPreferences.APP_DATA_PATH_DEFAULT
            DEFAULT_VALUES[READING_HISTORY_SIZE] = AppPreferences.READING_HISTORY_SIZE_DEFAULT
            DEFAULT_VALUES[CURRENT_QURAN_TEXT_VERSION] = AppPreferences.CURRENT_QURAN_TEXT_VERSION_DEFAULT
            DEFAULT_VALUES[CURRENT_RECITATION_ID] = AppPreferences.CURRENT_RECITATION_ID_DEFAULT
            DEFAULT_VALUES[CURRENT_WORD_BY_WORD_TRANSLATION] = AppPreferences.CURRENT_WORD_BY_WORD_TRANSLATION_DEFAULT
            DEFAULT_VALUES[IS_INITIAL_SETUP_COMPLETED] = AppPreferences.IS_INITIAL_SETUP_COMPLETED_DEFAULT
            DEFAULT_VALUES[IS_MUSHAF_MODE] = AppPreferences.IS_MUSHAF_MODE_DEFAULT
            DEFAULT_VALUES[LANGUAGES_LAST_UPDATE] = AppPreferences.LANGUAGES_LAST_UPDATE_DEFAULT
            DEFAULT_VALUES[LAST_READ_SURAH] = AppPreferences.LAST_READ_SURAH_DEFAULT
            DEFAULT_VALUES[LAST_READ_AYAH] = AppPreferences.LAST_READ_AYAH_DEFAULT
            DEFAULT_VALUES[OPEN_LAST_READING_PLACE_ON_START_ENABLED] = AppPreferences.OPEN_LAST_READING_PLACE_ON_START_ENABLED_DEFAULT
            DEFAULT_VALUES[QURAN_ARABIC_FONT] = AppPreferences.QURAN_ARABIC_FONT_DEFAULT
            DEFAULT_VALUES[RECITATIONS_LIST_UPDATE_TIME] = AppPreferences.RECITATIONS_LIST_UPDATE_TIME_DEFAULT
            DEFAULT_VALUES[SUGGEST_DOWNLOAD_IMAGES_BUNDLE] = AppPreferences.SUGGEST_DOWNLOAD_IMAGES_BUNDLE_DEFAULT
            DEFAULT_VALUES[IS_TRANSLATIONS_COPYRIGHT_DIALOG_SHOWED] = AppPreferences.IS_TRANSLATIONS_COPYRIGHT_DIALOG_SHOWED_DEFAULT
            DEFAULT_VALUES[SURAHS_LIST_SCROLL_POSITION] = AppPreferences.SURAHS_LIST_SCROLL_POSITION_DEFAULT
            DEFAULT_VALUES[TAJWEED_ENABLED] = AppPreferences.TAJWEED_ENABLED_DEFAULT
            DEFAULT_VALUES[PLAYER_AUTO_SCROLL_ENABLED] = AppPreferences.PLAYER_AUTO_SCROLL_ENABLED_DEFAULT
            DEFAULT_VALUES[WORDS_HIGHLIGHTING_ENABLED] = AppPreferences.WORDS_HIGHLIGHTING_ENABLED_DEFAULT
            DEFAULT_VALUES[TRANSLATIONS_LAST_UPDATE] = AppPreferences.TRANSLATIONS_LAST_UPDATE_DEFAULT
            DEFAULT_VALUES[TRANSLATIONS_UPDATES_CHECKING_ENABLED] = AppPreferences.TRANSLATIONS_UPDATES_CHECKING_ENABLED_DEFAULT
            DEFAULT_VALUES[TRANSLATIONS_UPDATES_NOTIFICATION_SHOWING_TIME] = AppPreferences.TRANSLATIONS_UPDATES_NOTIFICATION_SHOWING_TIME_DEFAULT
            DEFAULT_VALUES[WORD_BY_WORD_TRANSLATIONS_LAST_UPDATE] = AppPreferences.WORD_BY_WORD_TRANSLATIONS_LAST_UPDATE_DEFAULT
            DEFAULT_VALUES[WORD_BY_WORD_ENABLED] = AppPreferences.WORD_BY_WORD_ENABLED_DEFAULT
            DEFAULT_VALUES[TRANSLATIONS_FOR_SEARCH] = AppPreferences.TRANSLATIONS_FOR_SEARCH_DEFAULT
            DEFAULT_VALUES[MUSHAF_PAGE_TYPE] = AppPreferences.MUSHAF_PAGE_TYPE_DEFAULT
        }
    }

}