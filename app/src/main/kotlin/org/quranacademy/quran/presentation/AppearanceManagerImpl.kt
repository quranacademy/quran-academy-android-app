package org.quranacademy.quran.presentation

import org.quranacademy.quran.R
import org.quranacademy.quran.data.prefs.AppearancePreferences
import org.quranacademy.quran.domain.models.AppTheme
import org.quranacademy.quran.domain.models.ArabicFont
import org.quranacademy.quran.domain.models.TranslationFont
import org.quranacademy.quran.presentation.ui.appearance.AppearanceManager
import javax.inject.Inject

class AppearanceManagerImpl @Inject constructor(
        private val appearancePreferences: AppearancePreferences
) : AppearanceManager {

    override fun getCurrentAppTheme(): AppTheme = appearancePreferences.getCurrentAppTheme()

    override fun getCurrentAppThemeResId(): Int {
        return getAppThemeResId(getCurrentAppTheme())
    }

    override fun getAppThemeResId(appTheme: AppTheme): Int {
        return when (appTheme) {
            AppTheme.LIGHT -> R.style.QuranAcademyThemeLight
            AppTheme.BROWN -> R.style.QuranAcademyThemeBrown
            AppTheme.NIGHT -> R.style.QuranAcademyThemeNight
        }
    }

    override fun getCurrentMushafTheme(): AppTheme = appearancePreferences.getCurrentMushafTheme()

    override fun getCurrentMushafThemeResId(): Int {
        return when (getCurrentMushafTheme()) {
            AppTheme.LIGHT -> R.style.QuranAcademyThemeLight
            AppTheme.BROWN -> R.style.QuranAcademyThemeBrown
            AppTheme.NIGHT -> R.style.QuranAcademyThemeNight
        }
    }

    override fun getArabicFont(): ArabicFont = appearancePreferences.getArabicFont()

    override fun getTranslationFont(): TranslationFont = appearancePreferences.getTranslationFont()

    override fun getArabicTextSize(): Int = appearancePreferences.getArabicTextSize()

    override fun getTranslationTextSize(): Int = appearancePreferences.getTranslationTextSize()

    override fun getWbwTranslationTextSize(): Int = appearancePreferences.getWbwTranslationTextSize()

    override fun isQuranTextCenteringEnabled(): Boolean = appearancePreferences.isQuranTextCenteringEnabled()

    override fun isTranslationTextCenteringEnabled(): Boolean = appearancePreferences.isTranslationTextCenteringEnabled()

    override fun isTajweedEnabled(): Boolean = appearancePreferences.isTajweedEnabled()

    override fun getAppThemeUpdates() = appearancePreferences.getAppThemeUpdates()

    override fun getMushafThemeUpdates() = appearancePreferences.getMushafThemeUpdates()

    override fun getArabicFontUpdates() = appearancePreferences.getArabicFontUpdates()

    override fun getTranslationFontUpdates() = appearancePreferences.getTranslationFontUpdates()

    override fun getArabicTextSizeUpdates() = appearancePreferences.getArabicTextSizeUpdates()

    override fun getTranslationTextSizeUpdates() = appearancePreferences.getTranslationTextSizeUpdates()

    override fun getWbwTranslationTextSizeUpdates() = appearancePreferences.getWbwTranslationTextSizeUpdates()

    override fun getArabicTextCenteringEnabledUpdates() = appearancePreferences.getQuranTextCenteringEnabledUpdates()

    override fun getTranslationTextCenteringEnabledUpdates() = appearancePreferences.getTranslationTextCenteringEnabledUpdates()

    override fun isTajweedEnabledUpdates() = appearancePreferences.isTajweedEnabledUpdates()

}
