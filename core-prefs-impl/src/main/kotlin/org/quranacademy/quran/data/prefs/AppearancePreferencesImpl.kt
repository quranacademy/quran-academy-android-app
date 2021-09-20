package org.quranacademy.quran.data.prefs

import android.content.Context
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.quranacademy.quran.domain.models.AppTheme
import org.quranacademy.quran.domain.models.ArabicFont
import org.quranacademy.quran.domain.models.TranslationFont
import javax.inject.Inject

class AppearancePreferencesImpl @Inject constructor(
        context: Context
) : BasePreferences(context), AppearancePreferences {

    override fun getCurrentAppTheme(): AppTheme {
        val currentThemeCodeName = getString(APP_THEME)
        return getAppTheme(currentThemeCodeName)
    }

    override fun setCurrentAppTheme(theme: AppTheme) {
        putString(APP_THEME, theme.codeName)
    }

    override fun getCurrentMushafTheme(): AppTheme {
        val currentThemeCodeName = getString(MUSHAF_THEME)
        val mushafTheme = AppTheme.findThemeByCodeName(currentThemeCodeName!!)
        //тема может быть null, если она была удалена в новой версии приложения
        return mushafTheme ?: AppearancePreferences.MUSHAF_THEME_DEFAULT
    }

    override fun setCurrentMushafTheme(theme: AppTheme) {
        putString(MUSHAF_THEME, theme.codeName)
    }

    override fun setArabicFont(font: ArabicFont) {
        putString(ARABIC_FONT, font.codeName)
    }

    override fun getArabicFont(): ArabicFont {
        val currentFontCodeName = getString(ARABIC_FONT)
        return getArabicFont(currentFontCodeName)
    }

    override fun setTranslationFont(font: TranslationFont) {
        putString(TRANSLATION_FONT, font.codeName)
    }

    override fun getTranslationFont(): TranslationFont {
        val currentFontCodeName = getString(TRANSLATION_FONT)
        return getTranslationFont(currentFontCodeName)
    }

    override fun getArabicTextSize(): Int {
        return getInt(QURAN_ARABIC_TEXT_SIZE)
    }

    override fun setArabicTextSize(textSize: Int) {
        putInt(QURAN_ARABIC_TEXT_SIZE, textSize)
    }

    override fun getTranslationTextSize(): Int {
        return getInt(QURAN_TRANSLATION_TEXT_SIZE)
    }

    override fun setTranslationTextSize(textSize: Int) {
        putInt(QURAN_TRANSLATION_TEXT_SIZE, textSize)
    }

    override fun getWbwTranslationTextSize(): Int {
        return getInt(QURAN_WBW_TRANSLATION_TEXT_SIZE)
    }

    override fun setWbwTranslationTextSize(textSize: Int) {
        putInt(QURAN_WBW_TRANSLATION_TEXT_SIZE, textSize)
    }

    override fun isQuranTextCenteringEnabled(): Boolean {
        return getBoolean(QURAN_TEXT_CENTERING_ENABLED)
    }

    override fun setCenterQuranTextEnabled(isEnabled: Boolean) {
        putBoolean(QURAN_TEXT_CENTERING_ENABLED, isEnabled)
    }

    override fun isTranslationTextCenteringEnabled(): Boolean {
        return getBoolean(TRANSLATION_TEXT_CENTERING_ENABLED)
    }

    override fun setCenterTranslationTextEnabled(isEnabled: Boolean) {
        putBoolean(TRANSLATION_TEXT_CENTERING_ENABLED, isEnabled)
    }

    override fun isTajweedEnabled(): Boolean {
        return getBoolean(IS_TAJWEED_ENABLED)
    }

    override fun setTajweedEnabled(isEnabled: Boolean) {
        putBoolean(IS_TAJWEED_ENABLED, isEnabled)
    }

    override fun getAppThemeUpdates(): Flow<AppTheme> {
        return preferenceUpdatesObserver.observeString(APP_THEME)
                .map { getAppTheme(it) }
    }

    override fun getMushafThemeUpdates(): Flow<AppTheme> {
        return preferenceUpdatesObserver.observeString(MUSHAF_THEME)
                .map { getAppTheme(it) }
    }

    override fun getArabicFontUpdates(): Flow<ArabicFont> {
        return preferenceUpdatesObserver.observeString(ARABIC_FONT)
                .map { getArabicFont(it) }
    }

    override fun getTranslationFontUpdates(): Flow<TranslationFont> {
        return preferenceUpdatesObserver.observeString(TRANSLATION_FONT)
                .map { getTranslationFont(it) }
    }

    override fun getArabicTextSizeUpdates(): Flow<Int> {
        return preferenceUpdatesObserver.observeInt(QURAN_ARABIC_TEXT_SIZE)
    }

    override fun getTranslationTextSizeUpdates(): Flow<Int> {
        return preferenceUpdatesObserver.observeInt(QURAN_TRANSLATION_TEXT_SIZE)
    }

    override fun getWbwTranslationTextSizeUpdates(): Flow<Int> {
        return preferenceUpdatesObserver.observeInt(QURAN_WBW_TRANSLATION_TEXT_SIZE)
    }

    override fun getQuranTextCenteringEnabledUpdates(): Flow<Boolean> {
        return preferenceUpdatesObserver.observeBoolean(QURAN_TEXT_CENTERING_ENABLED)
    }

    override fun getTranslationTextCenteringEnabledUpdates(): Flow<Boolean> {
        return preferenceUpdatesObserver.observeBoolean(TRANSLATION_TEXT_CENTERING_ENABLED)
    }

    override fun isTajweedEnabledUpdates(): Flow<Boolean> {
        return preferenceUpdatesObserver.observeBoolean(IS_TAJWEED_ENABLED)
    }

    private fun getAppTheme(currentThemeCodeName: String?): AppTheme {
        val appTheme = AppTheme.findThemeByCodeName(currentThemeCodeName!!)
        //тема может быть null, если она была удалена в новой версии приложения
        return appTheme ?: AppearancePreferences.APP_THEME_DEFAULT
    }

    private fun getArabicFont(currentFontCodeName: String?): ArabicFont {
        val arabicFont = ArabicFont.findFontByCodeName(currentFontCodeName!!)
        //шрифт может быть null, если он был удалена в новой версии приложения
        return arabicFont ?: AppearancePreferences.ARABIC_FONT_DEFAULT
    }

    private fun getTranslationFont(currentFontCodeName: String?): TranslationFont {
        val translationFont = TranslationFont.findFontByCodeName(currentFontCodeName!!)
        //шрифт может быть null, если он был удалена в новой версии приложения
        return translationFont ?: AppearancePreferences.TRANSLATION_FONT_DEFAULT
    }

    companion object {

        const val APP_THEME = "app_theme"
        const val MUSHAF_THEME = "mushaf_theme"

        const val ARABIC_FONT = "arabic_font"
        const val TRANSLATION_FONT = "translation_font"

        const val QURAN_ARABIC_TEXT_SIZE = "quran_arabic_text_size"
        const val QURAN_TRANSLATION_TEXT_SIZE = "quran_translation_text_size"
        const val QURAN_WBW_TRANSLATION_TEXT_SIZE = "quran_wbw_translation_text_size"

        const val QURAN_TEXT_CENTERING_ENABLED = "quran_text_centering_enabled"
        const val TRANSLATION_TEXT_CENTERING_ENABLED = "translation_text_centering_enabled"

        const val IS_TAJWEED_ENABLED = "is_tajweed_enabled"

        init {
            DEFAULT_VALUES[APP_THEME] = AppearancePreferences.APP_THEME_DEFAULT.codeName
            DEFAULT_VALUES[MUSHAF_THEME] = AppearancePreferences.MUSHAF_THEME_DEFAULT.codeName
            DEFAULT_VALUES[ARABIC_FONT] = AppearancePreferences.ARABIC_FONT_DEFAULT.codeName
            DEFAULT_VALUES[TRANSLATION_FONT] = AppearancePreferences.TRANSLATION_FONT_DEFAULT.codeName
            DEFAULT_VALUES[QURAN_ARABIC_TEXT_SIZE] = AppearancePreferences.QURAN_ARABIC_TEXT_SIZE_DEFAULT
            DEFAULT_VALUES[QURAN_TRANSLATION_TEXT_SIZE] = AppearancePreferences.QURAN_TRANSLATION_TEXT_SIZE_DEFAULT
            DEFAULT_VALUES[QURAN_WBW_TRANSLATION_TEXT_SIZE] = AppearancePreferences.QURAN_WBW_TRANSLATION_TEXT_SIZE_DEFAULT
            DEFAULT_VALUES[QURAN_TEXT_CENTERING_ENABLED] = AppearancePreferences.ARABIC_TEXT_CENTERING_ENABLED_DEFAULT
            DEFAULT_VALUES[TRANSLATION_TEXT_CENTERING_ENABLED] = AppearancePreferences.TRANSLATION_TEXT_CENTERING_ENABLED_DEFAULT
            DEFAULT_VALUES[IS_TAJWEED_ENABLED] = AppearancePreferences.IS_TAJWEED_ENABLED_DEFAULT
        }

    }

}