package org.quranacademy.quran.player.presentation.playeroptions

import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import android.view.ViewTreeObserver
import android.widget.FrameLayout
import androidx.core.view.isGone
import androidx.core.view.isVisible
import com.afollestad.assent.Permission
import com.afollestad.assent.askForPermissions
import com.afollestad.materialdialogs.MaterialDialog
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.android.synthetic.main.fragment_player_options.*
import org.quranacademy.quran.core.permissions.createHQAPermissionsRationale
import org.quranacademy.quran.core.permissions.isPermissionBlockedFromAsking
import org.quranacademy.quran.core.permissions.showGrantPermissionFromSettingsDialog
import org.quranacademy.quran.di.get
import org.quranacademy.quran.di.installModule
import org.quranacademy.quran.di.namedBind
import org.quranacademy.quran.domain.models.AyahId
import org.quranacademy.quran.domain.models.FileDownloadInfo
import org.quranacademy.quran.player.R
import org.quranacademy.quran.player.di.playeroptions.EndAyah
import org.quranacademy.quran.player.di.playeroptions.StartAyah
import org.quranacademy.quran.presentation.mvp.routing.screens.AyahSelectionScreen
import org.quranacademy.quran.presentation.ui.base.BaseBottomSheetFragment
import org.quranacademy.quran.presentation.ui.dialogs.DownloadFileProgressDialog
import org.quranacademy.quran.presentation.ui.global.getCurrentAppLanguage
import org.quranacademy.quran.presentation.ui.global.isArabic
import toothpick.Scope

class PlayerOptionsFragment : BaseBottomSheetFragment(), PlayerOptionsView {

    override val layoutRes: Int = R.layout.fragment_player_options

    override val scopeModuleInstaller: (Scope) -> Unit = { scope ->
        val startAyah = requireArguments().getSerializable(START_AYAH_ARG) as AyahId
        val endAyah = requireArguments().getSerializable(END_AYAH_ARG) as AyahId
        scope.installModule {
            namedBind<StartAyah, AyahId>(startAyah)
            namedBind<EndAyah, AyahId>(endAyah)
        }
    }
    var onStartPlayingListener: (() -> Unit)? = null
    private var timecodesDownloadingProgressDialog: DownloadFileProgressDialog? = null

    @InjectPresenter
    lateinit var presenter: PlayerOptionsPresenter

    @ProvidePresenter
    fun providePresenter() = scope.get<PlayerOptionsPresenter>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        reciterValue.setOnClickListener {
            presenter.onRecitationClicked()
        }

        highlightWordsCheckbox.setOnCheckedChangeListener { buttonView, isChecked ->
            presenter.onHighlightWordsChanged(isChecked)
        }

        autoScrollEnabledCheckbox.setOnCheckedChangeListener { buttonView, isChecked ->
            presenter.onAutoScrollEnabledChanged(isChecked)
        }

        playFromValue.setOnClickListener {
            presenter.onStartAyahClicked()
        }

        playToValue.setOnClickListener {
            presenter.onEndAyahClicked()
        }

        rangeRepetitionsCountValue.setOnClickListener {
            presenter.onRangeRepetitionsCountClicked()
        }

        singleAyahRepetitionsCountValue.setOnClickListener {
            presenter.onAyahRepetitionsCountClicked()
        }

        //открывает панельку на всю высоту
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

        startPlaybackButton.setOnClickListener { startPlayback() }
    }

    fun onAyahSelected(ayahId: AyahId, type: AyahSelectionScreen.Type) {
        if (type == AyahSelectionScreen.Type.START) {
            presenter.onStartAyahSelected(ayahId)
        } else {
            presenter.onEndAyahSelected(ayahId)
        }
    }

    override fun showPlayerOptions(
            recitationName: String,
            autoScrollEnabled: Boolean,
            showHighlightWordsOption: Boolean,
            highlightWords: Boolean,
            startSurahName: String,
            startAyah: AyahId,
            endSurahName: String,
            endAyah: AyahId,
            rangeRepetitionsCount: Int,
            ayahRepetitionsCount: Int
    ) {
        playerOptionsContent.isVisible = true
        val isCurrentLanguageArabic = getCurrentAppLanguage().isArabic()
        val rangeRepetitionsCountString = RepetitionsCountDialog.getRepetitionsCountRepresentation(rangeRepetitionsCount, isCurrentLanguageArabic)
        val ayahRepetitionsCountString = RepetitionsCountDialog.getRepetitionsCountRepresentation(ayahRepetitionsCount, isCurrentLanguageArabic)

        autoScrollEnabledCheckbox.isChecked = autoScrollEnabled
        //highlightWordsCheckbox.visible(showHighlightWordsOption)
        highlightWordsCheckbox.isChecked = highlightWords
        reciterValue.text = recitationName
        playFromValue.text = getString(R.string.ayah_number_option_value, startSurahName, startAyah.ayahNumber)
        playToValue.text = getString(R.string.ayah_number_option_value, endSurahName, endAyah.ayahNumber)

        fun Int.isEndless(): Boolean = this == RepetitionsCountDialog.ENDLESS_REPEAT

        rangeRepetitionsCountValue.text = if (!rangeRepetitionsCount.isEndless()) {
            val timesLabelFormatted = resources.getQuantityString(R.plurals.repetition_count_option_value, rangeRepetitionsCount)
            String.format(timesLabelFormatted, rangeRepetitionsCountString)
        } else {
            rangeRepetitionsCountString
        }

        singleAyahRepetitionsCountValue.text = if (!ayahRepetitionsCount.isEndless()) {
            val timesLabelFormatted = resources.getQuantityString(R.plurals.repetition_count_option_value, ayahRepetitionsCount)
            String.format(timesLabelFormatted, ayahRepetitionsCountString)
        } else {
            ayahRepetitionsCountString
        }
    }

    override fun showRecitationsLoadingProgress(isVisible: Boolean) {
        playerOptionsInitializationProgress.isGone = !isVisible
    }

    override fun showRecitationsLoadingError(isVisible: Boolean) {
        if (isVisible) {
            playerOptionsContent.isGone = true
            playerOptionsInitializationError.isVisible = true
            playerInitializationErrorMessage.text = getString(R.string.no_network_connection_message)
            retryPlayerOptionsInitializationButton.setOnClickListener {
                presenter.onRetryLoadRecitations()
            }
        } else {
            playerOptionsInitializationError.isGone = !isVisible
        }
    }

    override fun showTimecodesDownloadingNeededDialog() {
        MaterialDialog(requireContext()).show {
            cancelOnTouchOutside(false)
            cancelable(false)
            message(R.string.timecodes_download_message)
            negativeButton(R.string.btn_cancel_title) {
                presenter.onCancelTimecodesDownloadingClicked()
            }
            positiveButton(R.string.btn_download_label) {
                presenter.onDownloadTimecodesClicked()
            }
        }
    }

    override fun showTimecodesDownloadingProgress(isVisible: Boolean) {
        if (isVisible) {
            timecodesDownloadingProgressDialog = DownloadFileProgressDialog(requireContext())
            timecodesDownloadingProgressDialog?.show()
        } else {
            timecodesDownloadingProgressDialog?.dismiss()
        }
    }

    override fun updateTimecodesDownloadingProgress(progress: FileDownloadInfo) {
        timecodesDownloadingProgressDialog?.updateDownloadProgress(progress)
    }

    override fun showRangeRepetitionsSelectionDialog(count: Int) {
        RepetitionsCountDialog.newInstance(count)
                .onValueSelected { repetitionsCount ->
                    presenter.onRangeRepetitionsCountSelected(repetitionsCount)
                }
                .show(childFragmentManager, "RepetitionsCountDialog")
    }

    override fun showAyahRepetitionsSelectionDialog(count: Int) {
        RepetitionsCountDialog.newInstance(count)
                .onValueSelected { repetitionsCount ->
                    presenter.onAyahRepetitionCountSelected(repetitionsCount)
                }
                .show(childFragmentManager, "RepetitionsCountDialog")
    }

    private fun startPlayback() {
        val rationaleHandler = createHQAPermissionsRationale {
            onPermission(Permission.READ_EXTERNAL_STORAGE, R.string.external_permission_not_needed_message)
        }

        askForPermissions(Permission.READ_EXTERNAL_STORAGE, rationaleHandler = rationaleHandler) { result ->
            val grantResult = result.grantResults.first()
            if (grantResult == PackageManager.PERMISSION_GRANTED) {
                presenter.onPlayButtonClicked()
                onStartPlayingListener?.invoke()
                dismiss()
            } else {
                if (isPermissionBlockedFromAsking(Permission.READ_EXTERNAL_STORAGE)) {
                    showGrantPermissionFromSettingsDialog(requireContext())
                } else {
                    startPlayback()
                }
            }
        }
    }


    companion object {

        const val START_AYAH_ARG = "start_ayah"
        const val END_AYAH_ARG = "end_ayah"

        fun newInstance(
                startAyah: AyahId,
                endAyah: AyahId
        ): PlayerOptionsFragment {
            val fragment = PlayerOptionsFragment()
            fragment.arguments = Bundle().apply {
                putSerializable(START_AYAH_ARG, startAyah)
                putSerializable(END_AYAH_ARG, endAyah)
            }
            return fragment
        }

    }


}