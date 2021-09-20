package org.quranacademy.quran.audiomanager.mvp.audiomanager

import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import org.quranacademy.quran.domain.models.Recitation
import org.quranacademy.quran.presentation.mvp.BaseMvpView
import org.quranacademy.quran.recitationsrepository.downloading.RecitationAudioDownloadInfo
import org.quranacademy.quran.recitationsrepository.recitationaudioinfo.RecitationAudioInfo

@StateStrategyType(AddToEndSingleStrategy::class)
interface AudioManagerView : BaseMvpView {

    fun showProgressLayout(isVisible: Boolean)

    fun updateRecitationsListVisibility(isVisible: Boolean)

    fun showNoNetworkLayout(isVisible: Boolean)

    fun showRecitations(recitationsAudioInfo: List<RecitationAudioInfo>)

    fun showRecitationAudioDownloadingProgress(recitation: Recitation)

    fun hideRecitationDownloadProgress()

    fun updateRecitationDownloadProgress(downloadInfo: RecitationAudioDownloadInfo)

}