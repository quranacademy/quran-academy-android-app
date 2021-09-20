package org.quranacademy.quran.wordbywordmanager.presentation.mvp

import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import org.quranacademy.quran.domain.models.FileDownloadInfo
import org.quranacademy.quran.presentation.mvp.BaseMvpView
import org.quranacademy.quran.wordbywordmanager.domain.WordByWordTranslationUIModel

@StateStrategyType(AddToEndSingleStrategy::class)
interface WordByWordTranslationsManagerView : BaseMvpView {

    fun showProgressLayout(isVisible: Boolean)

    fun updateTranslationsListVisibility(isVisible: Boolean)

    fun showNoNetworkLayout(isVisible: Boolean)

    fun showTranslations(
            translations: List<WordByWordTranslationUIModel>,
            isInitialSetupMode: Boolean
    )

    fun updateTranslations(
            translations: List<WordByWordTranslationUIModel>,
            isInitialSetupMode: Boolean
    )

    fun showTranslationDownloadingProgress(translation: WordByWordTranslationUIModel)

    fun hideTranslationDownloadProgress()

    fun updateTranslationDownloadProgress(downloadInfo: FileDownloadInfo)

    fun showDeleteTranslationDialog(translation: WordByWordTranslationUIModel)

}