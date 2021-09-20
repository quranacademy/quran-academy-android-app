package org.quranacademy.quran.player.presentation.playercontrol

import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import org.quranacademy.quran.domain.models.AyahAudio
import org.quranacademy.quran.presentation.mvp.BaseMvpView

@StateStrategyType(AddToEndSingleStrategy::class)
interface PlayerControlView : BaseMvpView {

    fun updateCurrentAudioInfo(ayahAudio: AyahAudio)

    fun updatePlayPauseButton(isPlay: Boolean)

    fun setPlayPauseButtonEnabled(isEnabled: Boolean)

    fun showCurrentAudioLoading(isLoading: Boolean)

    fun updateProgress(progress: Long)

    fun showCurrentAudioPlaybackStatus(status: String)

    fun disableControlButtons()

}