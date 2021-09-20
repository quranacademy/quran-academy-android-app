package org.quranacademy.quran.translationsmanager.presentation.mvp.translationordering

import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import org.quranacademy.quran.presentation.mvp.BaseMvpView
import org.quranacademy.quran.translationsmanager.presentation.mvp.global.TranslationItem

@StateStrategyType(AddToEndSingleStrategy::class)
interface TranslationsSortingView : BaseMvpView {

    fun showProgressLayout(isVisible: Boolean)

    fun showTranslations(translations: List<TranslationItem>)

    fun showTranslationsListEmpty()

}