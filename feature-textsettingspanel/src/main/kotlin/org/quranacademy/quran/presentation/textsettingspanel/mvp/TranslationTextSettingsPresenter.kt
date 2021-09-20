package org.quranacademy.quran.presentation.textsettingspanel.mvp

import com.arellomobile.mvp.InjectViewState
import org.quranacademy.quran.data.prefs.AppearancePreferences
import org.quranacademy.quran.domain.models.ArabicFont
import org.quranacademy.quran.domain.models.TranslationFont
import org.quranacademy.quran.presentation.mvp.BasePresenter
import javax.inject.Inject

@InjectViewState
class TranslationTextSettingsPresenter @Inject constructor(
        private val appearancePreferences: AppearancePreferences
) : BasePresenter<TranslationTextSettingsView>() {

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()

        viewState.initScreen(
                arabicTextSize = appearancePreferences.getArabicTextSize(),
                translationTextSize = appearancePreferences.getTranslationTextSize(),
                wbwTranslationTextSize = appearancePreferences.getWbwTranslationTextSize(),
                quranTextCenteringEnabled = appearancePreferences.isQuranTextCenteringEnabled(),
                isTajweedEnabled = appearancePreferences.isTajweedEnabled(),
                translationTextCenteringEnabled = appearancePreferences.isTranslationTextCenteringEnabled(),
                arabicFont = appearancePreferences.getArabicFont(),
                translationFont = appearancePreferences.getTranslationFont()
        )
    }

    fun setArabicTextSize(textSize: Int) = appearancePreferences.setArabicTextSize(textSize)

    fun setTranslationTextSize(textSize: Int) = appearancePreferences.setTranslationTextSize(textSize)

    fun setWbwTranslationTextSize(textSize: Int) = appearancePreferences.setWbwTranslationTextSize(textSize)

    fun setQuranTextCenteringEnabled(isEnabled: Boolean) = appearancePreferences.setCenterQuranTextEnabled(isEnabled)

    fun setTranslationTextCenteringEnabled(isEnabled: Boolean) = appearancePreferences.setCenterTranslationTextEnabled(isEnabled)

    fun setTajweedHighlightingEnabled(isEnabled: Boolean) = appearancePreferences.setTajweedEnabled(isEnabled)

    fun onSelectArabicFontClicked() {
        viewState.showArabicFontSelectionDialog(ArabicFont.values().toList())
    }

    fun onArabicFontSelected(font: ArabicFont) {
        appearancePreferences.setArabicFont(font)
    }

    fun onSelectTranslationFontClicked() {
        viewState.showTranslationFontSelectionDialog(TranslationFont.values().toList())
    }

    fun onTranslationFontSelected(font: TranslationFont) {
        appearancePreferences.setTranslationFont(font)
    }

}