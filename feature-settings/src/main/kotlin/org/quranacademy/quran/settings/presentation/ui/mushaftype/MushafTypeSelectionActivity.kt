package org.quranacademy.quran.settings.presentation.ui.mushaftype

import android.os.Bundle
import com.afollestad.materialdialogs.MaterialDialog
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import kotlinx.android.synthetic.main.activity_page_select.*
import org.quranacademy.quran.di.get
import org.quranacademy.quran.domain.models.FileDownloadInfo
import org.quranacademy.quran.presentation.ui.base.BaseThemedActivity
import org.quranacademy.quran.presentation.ui.dialogs.DownloadFileProgressDialog
import org.quranacademy.quran.settings.R
import org.quranacademy.quran.settings.presentation.mvp.mushaftype.MushafTypeSelectionPresenter
import org.quranacademy.quran.settings.presentation.mvp.mushaftype.MushafTypeSelectionView
import org.quranacademy.quran.settings.presentation.mvp.mushaftype.PageTypeInfo

class MushafTypeSelectionActivity : BaseThemedActivity(), MushafTypeSelectionView {

    override val layoutRes = R.layout.activity_page_select

    private var boundsDownloadingProgress: DownloadFileProgressDialog? = null

    @InjectPresenter
    lateinit var presenter: MushafTypeSelectionPresenter

    @ProvidePresenter
    fun providePresenter() = scope.get<MushafTypeSelectionPresenter>()

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun showPageTypes(types: List<PageTypeInfo>) {
        pageTypesPager.adapter = PageTypesAdapter(types) {
            presenter.onPageTypeSelected(it)
        }
    }

    override fun showMushafBoundsDownloadDialog(show: Boolean) {
        if (show) {
            boundsDownloadingProgress = DownloadFileProgressDialog(this)
            boundsDownloadingProgress?.show()
        } else {
            boundsDownloadingProgress?.dismiss()
        }
    }


    override fun updateMushafBoundsDownloadProgress(progress: FileDownloadInfo) {
        boundsDownloadingProgress?.updateDownloadProgress(progress)
    }

    override fun showBoundsDownloadRetryDialog(info: PageTypeInfo) {
        MaterialDialog(this).show {
            title(R.string.check_network_dialog_title)
            message(R.string.check_network_dialog_message)
            negativeButton(R.string.btn_cancel_title)
            positiveButton(R.string.btn_retry_label) {
                presenter.onRetryBundleDownloadingClicked(info)
            }
        }
    }

}
