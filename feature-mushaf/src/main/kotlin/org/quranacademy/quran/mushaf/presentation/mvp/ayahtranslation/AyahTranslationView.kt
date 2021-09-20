package org.quranacademy.quran.mushaf.presentation.mvp.ayahtranslation

import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import org.quranacademy.quran.domain.models.AyahDetails
import org.quranacademy.quran.presentation.mvp.BaseMvpView

@StateStrategyType(AddToEndSingleStrategy::class)
interface AyahTranslationView : BaseMvpView {

    fun showProgressLayout(isVisible: Boolean)

    fun updateAyahTranslationsVisibility(isVIsible: Boolean)

    fun showAyahTranslations(translations: List<AyahDetails>)

    fun showEnabledTranslationsNotFound(isVisible: Boolean)

    fun showNavigationButtons(prevButton: Boolean, nextButton: Boolean)

    fun switchToTranslation(currentAyahPosition: Int)

}