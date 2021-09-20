package org.quranacademy.quran.player.presentation.playercontrol

import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewTreeObserver
import android.widget.FrameLayout
import androidx.core.view.isGone
import androidx.core.view.isVisible
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.android.synthetic.main.fragment_player_control.*
import org.quranacademy.quran.di.bindSingleton
import org.quranacademy.quran.di.get
import org.quranacademy.quran.di.installModule
import org.quranacademy.quran.domain.models.AyahAudio
import org.quranacademy.quran.domain.models.PlaybackOptions
import org.quranacademy.quran.player.R
import org.quranacademy.quran.player.presentation.global.extensions.formatDuration
import org.quranacademy.quran.player.presentation.playeroptions.PlayerOptionsShell
import org.quranacademy.quran.presentation.ui.base.BaseBottomSheetFragment
import toothpick.Scope

class PlayerControlFragment : BaseBottomSheetFragment(), PlayerControlView {

    override val layoutRes: Int = R.layout.fragment_player_control

    override val scopeModuleInstaller: (Scope) -> Unit = {
        it.installModule {
            bindSingleton<PlayerOptionsShell>()
        }
    }

    private val progressTrackingDetector = ProgressTrackingDetector(
            onTrackingStarted = { presenter.onProgressTracking(true) },
            onProgressFinished = { progress ->
                presenter.onProgressSelected(progress.toLong())
                presenter.onProgressTracking(false)
            }
    )

    @InjectPresenter
    lateinit var presenter: PlayerControlPresenter

    @ProvidePresenter
    fun providePresenter() = scope.get<PlayerControlPresenter>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        playbackProgressView.setOnSeekBarChangeListener(progressTrackingDetector)

        playPauseButton.setOnClickListener {
            presenter.onPlayPauseButtonClicked()
        }

        prevAyahButton.setOnClickListener {
            presenter.onPrevAudioButtonClicked()
        }

        nextAyahButton.setOnClickListener {
            presenter.onNextButtonClicked()
        }

        stopPlayback.setOnClickListener {
            presenter.onStopButtonClicked()
        }

        openPlaybackSettings.setOnClickListener {
            presenter.onOpenPlaybackSettingsClicked()
        }

        view.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                view.viewTreeObserver.removeOnGlobalLayoutListener(this)
                val dialog = dialog as BottomSheetDialog?
                val bottomSheet = dialog!!.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet) as FrameLayout?
                val behavior = BottomSheetBehavior.from(bottomSheet!!)
                behavior.state = BottomSheetBehavior.STATE_EXPANDED
                behavior.peekHeight = 0 // Remove this line to hide a dark background if you manually hide the dialog.
            }
        })
    }

    fun onPlayerOptionsChanged(options: PlaybackOptions?) {
        presenter.onPlayerOptionsChanged(options)
    }

    override fun updateCurrentAudioInfo(ayahAudio: AyahAudio) {
        val duration = ayahAudio.duration
        playbackProgressView.max = duration.toInt()
        endTime.text = duration.formatDuration()
        currentAudio.text = getString(R.string.ayah_playback_title, ayahAudio.surahName, ayahAudio.ayahNumber)
    }

    override fun updatePlayPauseButton(isPlay: Boolean) {
        playPauseButton.change(isPlay)
    }

    override fun setPlayPauseButtonEnabled(isEnabled: Boolean) {
        playPauseButton.isClickable = isEnabled
    }

    override fun showCurrentAudioLoading(isLoading: Boolean) {
        if (isLoading) {
            audioDownloadProgress.isVisible = true
            audioDownloadProgress.spin()
        } else {
            audioDownloadProgress.stopSpinning()
        }
    }

    override fun updateProgress(progress: Long) {
        playbackProgressView.progress = progress.toInt()
        currentProgressTime.text = progress.formatDuration()
    }

    override fun showCurrentAudioPlaybackStatus(status: String) {
        currentAudioPlaybackStatus.isGone = !status.isNotEmpty()
        currentAudioPlaybackStatus.text = status
    }

    override fun disableControlButtons() {
        listOf(nextAyahButton, prevAyahButton, playPauseContainer, playbackProgressView).forEach { button ->
            button.isEnabled = false
            button.alpha = 0.5f
        }
    }

}