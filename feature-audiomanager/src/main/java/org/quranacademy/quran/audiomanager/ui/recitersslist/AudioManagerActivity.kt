package org.quranacademy.quran.audiomanager.ui.recitersslist

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.widget.Toolbar
import com.afollestad.materialdialogs.MaterialDialog
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import kotlinx.android.synthetic.main.activity_audio_manager.*
import org.quranacademy.quran.QuranConstants
import org.quranacademy.quran.audiomanager.R
import org.quranacademy.quran.audiomanager.mvp.audiomanager.AudioManagerPresenter
import org.quranacademy.quran.audiomanager.mvp.audiomanager.AudioManagerView
import org.quranacademy.quran.di.get
import org.quranacademy.quran.domain.models.FileDownloadInfo
import org.quranacademy.quran.domain.models.Recitation
import org.quranacademy.quran.presentation.extensions.visible
import org.quranacademy.quran.presentation.ui.base.BaseThemedActivity
import org.quranacademy.quran.presentation.ui.dialogs.DownloadFileProgressDialog
import org.quranacademy.quran.recitationsrepository.downloading.RecitationAudioDownloadInfo
import org.quranacademy.quran.recitationsrepository.recitationaudioinfo.RecitationAudioInfo

class AudioManagerActivity : BaseThemedActivity(), AudioManagerView {

    override val layoutRes = R.layout.activity_audio_manager

    @InjectPresenter
    lateinit var presenter: AudioManagerPresenter

    @ProvidePresenter
    fun providePresenter() = scope.get<AudioManagerPresenter>()

    private val toolbar: Toolbar by lazy { findViewById<Toolbar>(R.id.toolbar) }
    private val retryButton: Button by lazy { findViewById<Button>(R.id.retryButton) }
    private val recitationsAdapter by lazy {
        RecitationsAdapter(
                downloadClickListener = { presenter.onDownloadRecitationAudioClicked(it) },
                removeClickListener = { onDeleteRecitationAudioClicked(it) },
                openRecitationDetails = { presenter.onOpenRecitationDetailsClicked(it) }
        )
    }

    private var downloadProgressDialog: DownloadFileProgressDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        toolbar.setTitle(R.string.audio_manager)
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp)
        toolbar.setNavigationOnClickListener {
            onBackPressed()
        }

        recitationsRefreshLayout.setOnRefreshListener {
            presenter.refreshRecitationsList()
            recitationsRefreshLayout.isRefreshing = false
        }
        retryButton.setOnClickListener { presenter.onRetryLoadRecitationsListClicked() }

        recitationsList.adapter = recitationsAdapter
    }

    override fun showProgressLayout(isVisible: Boolean) {
        progressLayout.visible(isVisible)
    }

    override fun updateRecitationsListVisibility(isVisible: Boolean) {
        recitationsList.visible(isVisible)
    }

    override fun showNoNetworkLayout(isVisible: Boolean) {
        recitationsRefreshLayout.isEnabled = !isVisible
        noNetworkConnectionLayout.visible(isVisible)
    }

    override fun showRecitations(recitationsAudioInfo: List<RecitationAudioInfo>) {
        recitationsAdapter.setData(recitationsAudioInfo)
    }

    override fun showRecitationAudioDownloadingProgress(recitation: Recitation) {
        this.downloadProgressDialog = DownloadFileProgressDialog(this)
                .setTitle(getString(R.string.audio_downloading_title))
                .cancelButton { presenter.onCancelRecitationAudioDownloadingClicked() }
                .show()
        updateDownloadDialogMessage(recitation, 0)
    }

    override fun hideRecitationDownloadProgress() {
        downloadProgressDialog?.dismiss()
    }

    override fun updateRecitationDownloadProgress(downloadInfo: RecitationAudioDownloadInfo) {
        val progress = FileDownloadInfo(downloadInfo.downloadedSizeBytes, downloadInfo.totalSize)
        downloadProgressDialog?.updateDownloadProgress(progress, false)
        updateDownloadDialogMessage(downloadInfo.recitation, downloadInfo.surahNumber)
    }

    private fun onDeleteRecitationAudioClicked(recitation: Recitation) {
        MaterialDialog(this).show {
            title(R.string.remove_recitation_audio_title)
            message(text = getString(R.string.remove_recitation_audio_message, recitation.name))
            positiveButton(R.string.btn_remove_title) { presenter.onDeleteRecitationAudioClicked(recitation) }
            negativeButton(R.string.btn_cancel_title) { dismiss() }
        }
    }

    private fun updateDownloadDialogMessage(
            recitation: Recitation,
            downloadableSurahNumber: Int
    ) {
        val message = getString(
                R.string.audio_downloading_message,
                recitation.name,
                downloadableSurahNumber.toString(),
                QuranConstants.SURAHS_COUNT.toString()
        )
        downloadProgressDialog?.updateMessage(message)
    }

}