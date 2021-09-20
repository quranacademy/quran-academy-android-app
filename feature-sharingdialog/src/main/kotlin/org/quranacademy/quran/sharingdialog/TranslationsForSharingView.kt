package org.quranacademy.quran.sharingdialog

import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import org.quranacademy.quran.domain.models.Translation
import org.quranacademy.quran.presentation.mvp.BaseMvpView

@StateStrategyType(AddToEndSingleStrategy::class)
interface TranslationsForSharingView : BaseMvpView {

    fun showTranslations(translations: List<TranslationSharingModel>)

    fun updateButtonState(isEnabled: Boolean)

}