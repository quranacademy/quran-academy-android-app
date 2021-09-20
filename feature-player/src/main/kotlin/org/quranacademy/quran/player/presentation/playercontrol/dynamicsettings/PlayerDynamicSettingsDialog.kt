package org.quranacademy.quran.player.presentation.playercontrol.dynamicsettings

import android.app.Dialog
import android.os.Bundle
import android.view.View
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import kotlinx.android.synthetic.main.fragment_player_dynamic_settings.view.*
import org.quranacademy.quran.di.get
import org.quranacademy.quran.player.R
import org.quranacademy.quran.player.presentation.playeroptions.RepetitionsCountDialog
import org.quranacademy.quran.presentation.extensions.inflate
import org.quranacademy.quran.presentation.extensions.setSize
import org.quranacademy.quran.presentation.ui.base.BaseDialogFragment
import org.quranacademy.quran.presentation.ui.global.getCurrentAppLanguage
import org.quranacademy.quran.presentation.ui.global.isArabic

class PlayerDynamicSettingsDialog : BaseDialogFragment(), PlayerDynamicSettingsView {

    @InjectPresenter
    lateinit var presenter: PlayerDynamicSettingsPresenter

    private lateinit var dialogContentView: View

    @ProvidePresenter
    fun providePresenter() = scope.get<PlayerDynamicSettingsPresenter>()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        dialogContentView = inflate(R.layout.fragment_player_dynamic_settings)

        dialogContentView.reciterValue.setOnClickListener {
            presenter.onRecitationClicked()
        }

        dialogContentView.autoScrollEnabledCheckbox.setOnCheckedChangeListener { _, isChecked ->
            presenter.onAutoScrollEnabledChanged(isChecked)
        }

        dialogContentView.rangeRepetitionsCountValue.setOnClickListener {
            presenter.onRangeRepetitionsCountClicked()
        }

        dialogContentView.singleAyahRepetitionsCountValue.setOnClickListener {
            presenter.onAyahRepetitionsCountClicked()
        }

        return MaterialDialog(requireContext()).show {
            setSize(widthInPercents = 0.95f)
            customView(view = dialogContentView, noVerticalPadding = true)
            noAutoDismiss()
            cancelOnTouchOutside(false)
            negativeButton(R.string.btn_cancel_title) {
                presenter.onClosePlaybackSettingsClicked()
            }
            positiveButton(R.string.btn_label_ok) {
                presenter.onApplyPlaybackSettingsClicked()
            }
        }
    }

    override fun showPlaybackSettings(
            recitationName: String,
            autoScrollEnabled: Boolean,
            rangeRepetitionsCount: Int,
            ayahRepetitionsCount: Int
    ) {
        val isCurrentLanguageArabic = getCurrentAppLanguage().isArabic()
        val rangeRepetitionsCountString = RepetitionsCountDialog.getRepetitionsCountRepresentation(
                rangeRepetitionsCount,
                isCurrentLanguageArabic
        )
        val ayahRepetitionsCountString = RepetitionsCountDialog.getRepetitionsCountRepresentation(
                ayahRepetitionsCount,
                isCurrentLanguageArabic
        )

        dialogContentView.autoScrollEnabledCheckbox.isChecked = autoScrollEnabled
        dialogContentView.reciterValue.text = recitationName

        fun Int.isEndless(): Boolean = this == RepetitionsCountDialog.ENDLESS_REPEAT

        dialogContentView.rangeRepetitionsCountValue.text = if (!rangeRepetitionsCount.isEndless()) {
            val timesLabelFormatted = resources.getQuantityString(R.plurals.repetition_count_option_value, rangeRepetitionsCount)
            String.format(timesLabelFormatted, rangeRepetitionsCountString)
        } else {
            rangeRepetitionsCountString
        }

        dialogContentView.singleAyahRepetitionsCountValue.text = if (!ayahRepetitionsCount.isEndless()) {
            val timesLabelFormatted = resources.getQuantityString(R.plurals.repetition_count_option_value, ayahRepetitionsCount)
            String.format(timesLabelFormatted, ayahRepetitionsCountString)
        } else {
            ayahRepetitionsCountString
        }
    }

    override fun showRangeRepetitionsSelectionDialog(count: Int) {
        RepetitionsCountDialog.newInstance(count)
                .onValueSelected { repetitionsCount ->
                    presenter.onRangeRepetitionsCountSelected(repetitionsCount)
                }
                .show(childFragmentManager, RepetitionsCountDialog.TAG)
    }

    override fun showAyahRepetitionsSelectionDialog(count: Int) {
        RepetitionsCountDialog.newInstance(count)
                .onValueSelected { repetitionsCount ->
                    presenter.onAyahRepetitionCountSelected(repetitionsCount)
                }
                .show(childFragmentManager, RepetitionsCountDialog.TAG)
    }

}