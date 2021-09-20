package org.quranacademy.quran.presentation.textsettingspanel.mvp

import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy
import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import org.quranacademy.quran.domain.models.ArabicFont
import org.quranacademy.quran.domain.models.TranslationFont
import org.quranacademy.quran.presentation.mvp.BaseMvpView

@StateStrategyType(AddToEndSingleStrategy::class)
interface TranslationTextSettingsView : BaseMvpView {

    fun initScreen(
            arabicTextSize: Int,
            translationTextSize: Int,
            wbwTranslationTextSize: Int,
            quranTextCenteringEnabled: Boolean,
            translationTextCenteringEnabled: Boolean,
            isTajweedEnabled: Boolean,
            arabicFont: ArabicFont,
            translationFont: TranslationFont
    )

    @StateStrategyType(OneExecutionStateStrategy::class)
    fun showArabicFontSelectionDialog(fonts: List<ArabicFont>)

    @StateStrategyType(OneExecutionStateStrategy::class)
    fun showTranslationFontSelectionDialog(fonts: List<TranslationFont>)

}