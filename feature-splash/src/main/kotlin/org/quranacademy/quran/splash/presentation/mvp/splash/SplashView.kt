package org.quranacademy.quran.splash.presentation.mvp.splash

import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import org.quranacademy.quran.domain.models.Language
import org.quranacademy.quran.presentation.mvp.BaseMvpView
import org.quranacademy.quran.splash.domain.splash.QuranDataDownloadingProgress

@StateStrategyType(AddToEndSingleStrategy::class)
interface SplashView : BaseMvpView {

    fun showLanguagesDialog(languages: List<Language>)

    fun showDownloadProgress(isVisible: Boolean)

    fun showDownloadProgress(progress: QuranDataDownloadingProgress)

    fun showCheckNetworkDialog()

    fun showPreparingProgress(isVisible: Boolean)

    fun hideStatusMessage()

}