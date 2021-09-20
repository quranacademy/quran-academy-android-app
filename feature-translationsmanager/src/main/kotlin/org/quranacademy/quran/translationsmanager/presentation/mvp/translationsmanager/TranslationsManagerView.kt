package org.quranacademy.quran.translationsmanager.presentation.mvp.translationsmanager

import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import org.quranacademy.quran.domain.models.FileDownloadInfo
import org.quranacademy.quran.presentation.mvp.BaseMvpView
import org.quranacademy.quran.translationsmanager.domain.translations.TranslationUIModel

@StateStrategyType(AddToEndSingleStrategy::class)
interface TranslationsManagerView : BaseMvpView {

    fun showProgressLayout(isVisible: Boolean)

    fun updateTranslationsListVisibility(isVisible: Boolean)

    fun showNoNetworkLayout(isVisible: Boolean)

    fun showTranslations(translations: List<TranslationUIModel>, isInitialSetupMode: Boolean)

    fun updateTranslations(translations: List<TranslationUIModel>, isInitialSetupMode: Boolean)

    fun updateTranslationDownloadProgress(translation: TranslationUIModel, downloadInfo: FileDownloadInfo?)

    fun showDeleteTranslationDialog(translation: TranslationUIModel)

}