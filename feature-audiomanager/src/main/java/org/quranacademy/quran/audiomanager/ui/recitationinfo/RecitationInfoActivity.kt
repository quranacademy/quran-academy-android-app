package org.quranacademy.quran.audiomanager.ui.recitationinfo

import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import com.afollestad.materialdialogs.MaterialDialog
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import kotlinx.android.synthetic.main.activity_recitation_info.*
import org.quranacademy.quran.audiomanager.R
import org.quranacademy.quran.audiomanager.di.RecitationId
import org.quranacademy.quran.audiomanager.mvp.reciteraudio.RecitationInfoPresenter
import org.quranacademy.quran.audiomanager.mvp.reciteraudio.RecitationInfoView
import org.quranacademy.quran.di.bindPrimitive
import org.quranacademy.quran.di.get
import org.quranacademy.quran.di.installModule
import org.quranacademy.quran.domain.models.FileDownloadInfo
import org.quranacademy.quran.presentation.extensions.visible
import org.quranacademy.quran.presentation.mvp.routing.screens.RecitationInfoScreen
import org.quranacademy.quran.presentation.ui.base.BaseThemedActivity
import org.quranacademy.quran.presentation.ui.dialogs.DownloadFileProgressDialog
import org.quranacademy.quran.recitationsrepository.recitationaudioinfo.RecitationSurahsAudioInfo
import org.quranacademy.quran.recitationsrepository.recitationaudioinfo.SurahAudioInfo
import toothpick.Scope

class RecitationInfoActivity : BaseThemedActivity(), RecitationInfoView {

    override val layoutRes = R.layout.activity_recitation_info

    @InjectPresenter
    lateinit var presenter: RecitationInfoPresenter

    override val scopeModuleInstaller: (Scope) -> Unit = {
        val screen = screenResolver.getScreen<RecitationInfoScreen>(this)
        it.installModule {
            bindPrimitive(RecitationId::class, screen.recitationId)
        }
    }

    @ProvidePresenter
    fun providePresenter() = scope.get<RecitationInfoPresenter>()

    private val toolbar: Toolbar by lazy { findViewById<Toolbar>(R.id.toolbar) }
    private val surahsAudioAdapter = SurahsAudioAdapter(
            downloadClickListener = { presenter.onDownloadSurahClicked(it) },
            removeClickListener = { onDeleteSurahAudioClicked(it) }
    )
    private var surahAudioDownloadingProgressDialog: DownloadFileProgressDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp)
        toolbar.setNavigationOnClickListener {
            onBackPressed()
        }

        surahsAudioInfoList.adapter = surahsAudioAdapter
    }

    override fun showSurahDownloadProgress(surahName: String) {
        this.surahAudioDownloadingProgressDialog = DownloadFileProgressDialog(this)
                .setTitle(getString(R.string.audio_downloading_title))
                .cancelButton { presenter.onCancelDownloadingClicked() }
                .show()
    }

    override fun hideSurahDownloadProgress() {
        surahAudioDownloadingProgressDialog?.dismiss()
        this.surahAudioDownloadingProgressDialog = null
    }

    override fun updateSurahDownloadProgress(
            surahName: String,
            ayahNumber: Int,
            ayahsCount: Int,
            progress: FileDownloadInfo
    ) {

        val message = getString(
                R.string.audio_downloading_message,
                surahName,
                ayahNumber.toString(),
                ayahsCount.toString()
        )
        surahAudioDownloadingProgressDialog?.updateMessage(message)
        surahAudioDownloadingProgressDialog?.updateDownloadProgress(progress, false)
    }

    override fun showRecitationInfo(recitationSurahsAudioInfo: RecitationSurahsAudioInfo) {
        toolbar.title = recitationSurahsAudioInfo.recitation.name
        surahsAudioAdapter.setData(recitationSurahsAudioInfo.surahsAudioInfo)
        progressLayout.visible(false)
    }

    private fun onDeleteSurahAudioClicked(surahAudioInfo: SurahAudioInfo) {
        MaterialDialog(this).show {
            title(R.string.remove_recitation_audio_title)
            message(text = getString(R.string.remove_recitation_audio_message, surahAudioInfo.surahName))
            negativeButton(R.string.btn_cancel_title)
            positiveButton(R.string.btn_label_ok) {
                presenter.onRemoveSurahClicked(surahAudioInfo)
            }
        }
    }

}