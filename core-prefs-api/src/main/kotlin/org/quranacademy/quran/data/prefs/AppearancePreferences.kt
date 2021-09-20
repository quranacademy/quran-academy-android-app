package org.quranacademy.quran.data.prefs

import kotlinx.coroutines.flow.Flow
import org.quranacademy.quran.domain.models.AppTheme
import org.quranacademy.quran.domain.models.ArabicFont
import org.quranacademy.quran.domain.models.TranslationFont

interface AppearancePreferences {

    companion object {
        val APP_THEME_DEFAULT = AppTheme.LIGHT
        val MUSHAF_THEME_DEFAULT = AppTheme.LIGHT

        val ARABIC_FONT_DEFAULT = ArabicFont.UTHMANIC_HAFS
        val TRANSLATION_FONT_DEFAULT = TranslationFont.ROBOTO

        const val QURAN_ARABIC_TEXT_SIZE_DEFAULT = 26
        const val QURAN_TRANSLATION_TEXT_SIZE_DEFAULT = 14
        const val QURAN_WBW_TRANSLATION_TEXT_SIZE_DEFAULT = 14

        const val ARABIC_TEXT_CENTERING_ENABLED_DEFAULT = false
        const val TRANSLATION_TEXT_CENTERING_ENABLED_DEFAULT = false

        const val IS_TAJWEED_ENABLED_DEFAULT = true
    }

    fun getCurrentAppTheme(): AppTheme

    fun setCurrentAppTheme(theme: AppTheme)

    fun getCurrentMushafTheme(): AppTheme

    fun setCurrentMushafTheme(theme: AppTheme)

    fun setArabicFont(font: ArabicFont)

    fun getArabicFont(): ArabicFont

    fun setTranslationFont(font: TranslationFont)

    fun getTranslationFont(): TranslationFont

    fun getArabicTextSize(): Int

    fun setArabicTextSize(textSize: Int)

    fun getTranslationTextSize(): Int

    fun setTranslationTextSize(textSize: Int)

    fun getWbwTranslationTextSize(): Int

    fun setWbwTranslationTextSize(textSize: Int)

    fun isQuranTextCenteringEnabled(): Boolean

    fun setCenterQuranTextEnabled(isEnabled: Boolean)

    fun isTranslationTextCenteringEnabled(): Boolean

    fun setCenterTranslationTextEnabled(isEnabled: Boolean)

    fun isTajweedEnabled(): Boolean

    fun setTajweedEnabled(isEnabled: Boolean)

    fun getAppThemeUpdates(): Flow<AppTheme>

    fun getMushafThemeUpdates(): Flow<AppTheme>

    fun getArabicFontUpdates(): Flow<ArabicFont>

    fun getTranslationFontUpdates(): Flow<TranslationFont>

    fun getArabicTextSizeUpdates(): Flow<Int>

    fun getTranslationTextSizeUpdates(): Flow<Int>

    fun getWbwTranslationTextSizeUpdates(): Flow<Int>

    fun getQuranTextCenteringEnabledUpdates(): Flow<Boolean>

    fun getTranslationTextCenteringEnabledUpdates(): Flow<Boolean>

    fun isTajweedEnabledUpdates(): Flow<Boolean>

}