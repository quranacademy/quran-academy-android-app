package org.quranacademy.quran.audiomanager.mvp.reciteraudio

import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import org.quranacademy.quran.domain.models.FileDownloadInfo
import org.quranacademy.quran.presentation.mvp.BaseMvpView
import org.quranacademy.quran.recitationsrepository.recitationaudioinfo.RecitationSurahsAudioInfo

@StateStrategyType(AddToEndSingleStrategy::class)
interface RecitationInfoView : BaseMvpView {

    fun showRecitationInfo(recitationSurahsAudioInfo: RecitationSurahsAudioInfo)

    fun showSurahDownloadProgress(surahName: String)

    fun updateSurahDownloadProgress(
            surahName: String,
            ayahNumber: Int,
            ayahsCount: Int,
            progress: FileDownloadInfo
    )

    fun hideSurahDownloadProgress()

}