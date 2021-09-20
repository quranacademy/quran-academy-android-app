package org.quranacademy.quran.memorization.ui

import android.os.Bundle
import android.view.WindowManager
import android.view.animation.AccelerateInterpolator
import androidx.appcompat.widget.Toolbar
import com.afollestad.materialdialogs.MaterialDialog
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import kotlinx.android.synthetic.main.activity_memorization.*
import org.quranacademy.quran.di.bind
import org.quranacademy.quran.di.get
import org.quranacademy.quran.di.installModule
import org.quranacademy.quran.domain.models.AyahId
import org.quranacademy.quran.domain.models.QuranPage
import org.quranacademy.quran.memorization.R
import org.quranacademy.quran.memorization.models.MemorizationOptions
import org.quranacademy.quran.memorization.mvp.memorization.MemorizationPresenter
import org.quranacademy.quran.memorization.mvp.memorization.MemorizationView
import org.quranacademy.quran.memorization.routing.MemorizationScreen
import org.quranacademy.quran.mushaf.presentation.mvp.mushafpage.AyahHighlightType
import org.quranacademy.quran.mushaf.presentation.ui.page.pagelayout.MushafPageLayout
import org.quranacademy.quran.presentation.extensions.enableStatusBarTranslucent
import org.quranacademy.quran.presentation.extensions.showFullScreen
import org.quranacademy.quran.presentation.ui.base.BaseThemedActivity
import org.quranacademy.quran.presentation.ui.dialogs.DownloadFileProgressDialog
import toothpick.Scope
import java.util.*

class MemorizationActivity : BaseThemedActivity(), MemorizationView {

    override val layoutRes = R.layout.activity_memorization

    private val toolbar: Toolbar by lazy { findViewById<Toolbar>(R.id.toolbar) }
    override val scopeModuleInstaller: (Scope) -> Unit = {
        val screen = screenResolver.getScreen<MemorizationScreen>(this)
        it.installModule {
            bind(MemorizationOptions::class)
                    .toInstance(screen.memorizationOptions)
        }
    }
    private var downloadProgressDialog: DownloadFileProgressDialog? = null
    private val pageLayout by lazy { findViewById<MushafPageLayout>(R.id.pageLayout) }

    @InjectPresenter
    lateinit var presenter: MemorizationPresenter

    @ProvidePresenter
    fun providePresenter() = scope.get<MemorizationPresenter>()

    override fun onCreate(savedInstanceState: Bundle?) {
        enableStatusBarTranslucent(true)
        super.onCreate(savedInstanceState)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp)
        toolbar.setNavigationOnClickListener { presenter.onBackPressed() }
        toolbar.setTitle(R.string.quran_memorization)

        pageLayout.mushafPageImage.setOnTouchListener(null)
        pageLayout.mushafPageImage.setOnClickListener {
            presenter.onPageClicked()
        }

        playPauseFab.setOnClickListener {
            presenter.onPlayPauseClicked()
        }
    }


    override fun showDataDownloadDialog(isVisible: Boolean) {
        if (isVisible) {
            this.downloadProgressDialog = DownloadFileProgressDialog(this)
                    .updateMessage(getString(R.string.memorize_data_downloading_message))
                    .indeterminate(true)
                    .show()
        } else {
            downloadProgressDialog?.dismiss()
        }
    }

    override fun showNoNetworkDialog() {
        MaterialDialog(this).show {
            title(R.string.no_network_connection_title)
            message(R.string.no_network_connection_message)
            negativeButton(R.string.btn_exit_label) {
                presenter.onExitConfirmed()
            }
            positiveButton(R.string.btn_retry_label) {
                presenter.retryDownloadClicked()
            }
        }
    }

    override fun showToolbar(isVisible: Boolean) {
        toolbarContainer.animate()
                .translationY(if (isVisible) 0f else -toolbarContainer.height.toFloat())
                .setInterpolator(AccelerateInterpolator(2f))
                .start()
        showFullScreen(isVisible)

        if (isVisible) playPauseFab.show() else playPauseFab.hide()
    }

    override fun showPage(pageInfo: QuranPage) {
        pageLayout.setPageInfo(pageInfo)
    }

    override fun updateAyahHighlighting(ayahHighlights: SortedMap<AyahHighlightType, MutableSet<AyahId>>) {
        pageLayout.updateAyahHighlighting(ayahHighlights)
    }

    override fun updatePlayPauseState(isPlaying: Boolean) {
        val iconResId = if (isPlaying) R.drawable.ic_pause_white else R.drawable.ic_play_white_24dp
        playPauseFab.setImageResource(iconResId)
    }

    override fun showMemorizationCompleted() {
        MaterialDialog(this).show {
            title(R.string.memorization_completed_title)
            message(R.string.memorization_completed_message)
            cancelable(false)
            cancelOnTouchOutside(false)
            positiveButton(R.string.btn_start_memorization_again_label) {
                presenter.onRestartMemorizationClicked()
            }
            negativeButton(R.string.btn_finish_label) {
                presenter.onFinishMemorizationClicked()
            }
        }
    }

    override fun showExitConfirmDialog() {
        MaterialDialog(this).show {
            message(R.string.exit_confirm_message)
            cancelable(false)
            cancelOnTouchOutside(false)
            positiveButton(R.string.btn_exit_label) {
                presenter.onExitConfirmed()
            }
            negativeButton(R.string.btn_cancel_title) {
                presenter.onExitCanceled()
            }
        }
    }

    override fun onBackPressed() = presenter.onBackPressed()

}