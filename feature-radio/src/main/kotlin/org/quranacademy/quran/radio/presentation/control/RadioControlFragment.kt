package org.quranacademy.quran.radio.presentation.control

import android.os.Bundle
import android.view.View
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import kotlinx.android.synthetic.main.fragment_radio_control.*
import org.quranacademy.quran.di.get
import org.quranacademy.quran.presentation.extensions.visible
import org.quranacademy.quran.presentation.ui.base.BaseFragment
import org.quranacademy.quran.radio.R

class RadioControlFragment : BaseFragment(), RadioControlView {

    companion object {
        fun newInstance(): RadioControlFragment {
            return RadioControlFragment()
        }
    }

    override val layoutRes = R.layout.fragment_radio_control

    @InjectPresenter
    lateinit var presenter: RadioControlPresenter

    @ProvidePresenter
    fun providePresenter() = scope.get<RadioControlPresenter>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        playPauseRadioButton.setOnClickListener {
            presenter.onPlayPauseButtonClicked()
        }
        stopRadioButton.setOnClickListener {
            presenter.onStopRadioClicked()
        }
    }

    override fun showRadioControl(isVisible: Boolean) {
        radioControlButton.visible(isVisible)
    }

    override fun changeRadioState(isPlaying: Boolean) {
        if (isPlaying) {
            playPauseRadioButton.labelText = getString(R.string.pause)
            playPauseRadioButton.setImageResource(R.drawable.ic_pause_white_24dp)
        } else {
            playPauseRadioButton.labelText = getString(R.string.play)
            playPauseRadioButton.setImageResource(R.drawable.ic_play_white_24dp)
        }
    }

}