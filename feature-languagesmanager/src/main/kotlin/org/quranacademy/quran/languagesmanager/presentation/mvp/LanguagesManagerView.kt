package org.quranacademy.quran.languagesmanager.presentation.mvp

import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import org.quranacademy.quran.presentation.mvp.BaseMvpView

@StateStrategyType(AddToEndSingleStrategy::class)
interface LanguagesManagerView : BaseMvpView {

    fun showProgressLayout(isVisible: Boolean)

    fun showNoNetworkLayout(isVisible: Boolean)

    fun showLanguages(languageCategories: List<LanguageCategoryViewModel>)

    fun updateLanguages(languageCategories: List<LanguageCategoryViewModel>)

    fun showLanguageDownloadProgress()

    fun hideLanguageDownloadProgress()

}