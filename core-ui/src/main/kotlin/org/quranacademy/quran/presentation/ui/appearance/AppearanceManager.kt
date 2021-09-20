package org.quranacademy.quran.presentation.ui.appearance

import kotlinx.coroutines.flow.Flow
import org.quranacademy.quran.domain.models.AppTheme
import org.quranacademy.quran.domain.models.ArabicFont
import org.quranacademy.quran.domain.models.TranslationFont

interface AppearanceManager {

    fun getCurrentAppTheme(): AppTheme

    fun getCurrentAppThemeResId(): Int

    fun getAppThemeResId(appTheme: AppTheme): Int

    fun getCurrentMushafTheme(): AppTheme

    fun getCurrentMushafThemeResId(): Int

    fun getArabicFont(): ArabicFont

    fun getTranslationFont(): TranslationFont

    fun getArabicTextSize(): Int

    fun getTranslationTextSize(): Int

    fun getWbwTranslationTextSize(): Int

    fun isQuranTextCenteringEnabled(): Boolean

    fun isTranslationTextCenteringEnabled(): Boolean

    fun isTajweedEnabled(): Boolean

    fun getArabicFontUpdates(): Flow<ArabicFont>

    fun getTranslationFontUpdates(): Flow<TranslationFont>

    fun getArabicTextSizeUpdates(): Flow<Int>

    fun getTranslationTextSizeUpdates(): Flow<Int>

    fun getWbwTranslationTextSizeUpdates(): Flow<Int>

    fun getArabicTextCenteringEnabledUpdates(): Flow<Boolean>

    fun getTranslationTextCenteringEnabledUpdates(): Flow<Boolean>

    fun isTajweedEnabledUpdates(): Flow<Boolean>

    fun getAppThemeUpdates(): Flow<AppTheme>

    fun getMushafThemeUpdates(): Flow<AppTheme>

}