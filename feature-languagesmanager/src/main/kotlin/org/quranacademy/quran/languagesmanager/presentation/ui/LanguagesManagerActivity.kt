package org.quranacademy.quran.languagesmanager.presentation.ui

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.widget.Toolbar
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import kotlinx.android.synthetic.main.activity_languages.*
import org.quranacademy.quran.di.get
import org.quranacademy.quran.languagesmanager.R
import org.quranacademy.quran.languagesmanager.presentation.mvp.LanguageCategoryViewModel
import org.quranacademy.quran.languagesmanager.presentation.mvp.LanguagesManagerPresenter
import org.quranacademy.quran.languagesmanager.presentation.mvp.LanguagesManagerView
import org.quranacademy.quran.presentation.extensions.visible
import org.quranacademy.quran.presentation.ui.base.BaseThemedActivity
import org.quranacademy.quran.presentation.ui.dialogs.DownloadFileProgressDialog

class LanguagesManagerActivity : BaseThemedActivity(), LanguagesManagerView {

    override val layoutRes = R.layout.activity_languages

    @InjectPresenter
    lateinit var presenter: LanguagesManagerPresenter

    private val languagesAdapter = LanguagesAdapter(
            downloadClickListener = { presenter.onDownloadLanguageClicked(it) },
            removeClickListener = { presenter.onRemoveLanguageClicked(it) },
            enableClickListener = { presenter.onEnableLanguageClicked(it) }
    )

    private var languageDownloadingProgressDialog: DownloadFileProgressDialog? = null
    private val toolbar: Toolbar by lazy { findViewById<Toolbar>(R.id.toolbar) }
    private val retryButton: Button by lazy { findViewById<Button>(R.id.retryButton) }

    @ProvidePresenter
    fun providePresenter() = scope.get<LanguagesManagerPresenter>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp)
        toolbar.setNavigationOnClickListener { onBackPressed() }
        toolbar.setTitle(R.string.settings)

        languagesList.adapter = languagesAdapter

        retryButton.setOnClickListener { presenter.onRetryLanguagesListLoadingClicked() }
    }

    override fun showProgressLayout(isVisible: Boolean) {
        progressLayout.visible(isVisible)
    }

    override fun showNoNetworkLayout(isVisible: Boolean) {
        noNetworkConnectionLayout.visible(isVisible)
    }

    override fun showLanguages(languageCategories: List<LanguageCategoryViewModel>) {
        languagesList.visible(true)
        languagesAdapter.setData(languageCategories)
    }

    override fun updateLanguages(languageCategories: List<LanguageCategoryViewModel>) {
        languagesAdapter.updateData(languageCategories)
    }

    override fun showLanguageDownloadProgress() {
        languageDownloadingProgressDialog = DownloadFileProgressDialog(this)
                .indeterminate(true)
                .show()
    }

    override fun hideLanguageDownloadProgress() {
        languageDownloadingProgressDialog?.dismiss()
    }

}