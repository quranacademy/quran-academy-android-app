package org.quranacademy.quran.wordbywordmanager.presentation.ui

import android.os.Bundle
import android.view.View
import android.widget.Button
import com.afollestad.materialdialogs.MaterialDialog
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import kotlinx.android.synthetic.main.fragment_word_by_word_translations.*
import me.aartikov.alligator.Navigator
import org.quranacademy.quran.di.bindPrimitive
import org.quranacademy.quran.di.get
import org.quranacademy.quran.di.getGlobal
import org.quranacademy.quran.di.installModule
import org.quranacademy.quran.domain.models.FileDownloadInfo
import org.quranacademy.quran.presentation.extensions.visible
import org.quranacademy.quran.presentation.ui.base.BaseFragment
import org.quranacademy.quran.presentation.ui.dialogs.DownloadFileProgressDialog
import org.quranacademy.quran.wordbywordmanager.R
import org.quranacademy.quran.wordbywordmanager.di.IsInitialSetupMode
import org.quranacademy.quran.wordbywordmanager.domain.WordByWordTranslationUIModel
import org.quranacademy.quran.wordbywordmanager.presentation.mvp.WordByWordTranslationsManagerPresenter
import org.quranacademy.quran.wordbywordmanager.presentation.mvp.WordByWordTranslationsManagerView
import toothpick.Scope

class WordByWordTranslationsManagerFragment : BaseFragment(), WordByWordTranslationsManagerView {

    companion object {

        // Данный фрагмент используется в двух местах:
        // 1. Диалог, показывающийся при начальной настройке
        // 2. Экоан управления переводами в настройка
        //
        // В первом случае внешний вид немного отличается, поэтому мы используем данный флаг
        private const val IS_INITIAL_SETUP_MODE = "is_initial_setup_mode"

        fun newInstance(isInitialSetupMode: Boolean): WordByWordTranslationsManagerFragment {
            val fragment = WordByWordTranslationsManagerFragment()
            val args = Bundle()
            args.putBoolean(IS_INITIAL_SETUP_MODE, isInitialSetupMode)
            fragment.arguments = args
            return fragment
        }

    }

    override val layoutRes = R.layout.fragment_word_by_word_translations

    @InjectPresenter
    lateinit var presenter: WordByWordTranslationsManagerPresenter

    private val translationsAdapter by lazy {
        WordByWordTranslationsAdapter(
                context = context!!,
                downloadClickListener = { translation -> presenter.onDownloadTranslationClicked(translation) },
                updateClickListener = { translation -> presenter.onUpdateTranslationClicked(translation) },
                deleteClickListener = { translation -> presenter.onDeleteTranslationClicked(translation) },
                enableClickListener = { translation -> presenter.onEnableTranslationClicked(translation) }
        )
    }
    private var downloadProgressDialog: DownloadFileProgressDialog? = null
    private val retryButton: Button by lazy { view!!.findViewById<Button>(R.id.retryButton) }

    override val scopeModuleInstaller: (Scope) -> Unit = { scope ->
        val isInitialSetupMode = arguments!!.getBoolean(IS_INITIAL_SETUP_MODE)
        scope.installModule {
            bindPrimitive(IsInitialSetupMode::class, isInitialSetupMode)
        }
    }

    @ProvidePresenter
    fun providePresenter() = scope.get<WordByWordTranslationsManagerPresenter>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        translationsList.adapter = translationsAdapter
        retryButton.setOnClickListener { presenter.onRetryTranslationsListLoadingClicked() }

        translationsRefreshLayout.setColorSchemeColors(resources.getColor(R.color.hqa_blue_100))
        translationsRefreshLayout.setOnRefreshListener {
            translationsRefreshLayout.isRefreshing = false
            presenter.refreshTranslationsList()
        }
    }

    override fun showNoNetworkLayout(isVisible: Boolean) {
        noNetworkConnectionLayout.visible(isVisible)
    }

    override fun showProgressLayout(isVisible: Boolean) {
        if (isVisible) {
            //to prevent refreshing when translations list is downloading
            translationsRefreshLayout.isEnabled = false
        }
        progressLayout.visible(isVisible)
    }

    override fun updateTranslationsListVisibility(isVisible: Boolean) {
        translationsRefreshLayout.visible(isVisible)
    }

    override fun showTranslations(
            translations: List<WordByWordTranslationUIModel>,
            isInitialSetupMode: Boolean
    ) {
        translationsRefreshLayout.isEnabled = !isInitialSetupMode
        translationsAdapter.setData(
                translations = translations,
                splitByCategory = !isInitialSetupMode
        )
    }

    override fun updateTranslations(
            translations: List<WordByWordTranslationUIModel>,
            isInitialSetupMode: Boolean
    ) {
        translationsAdapter.updateData(
                translations = translations,
                splitByCategory = !isInitialSetupMode
        )
    }

    override fun showTranslationDownloadingProgress(translation: WordByWordTranslationUIModel) {
        this.downloadProgressDialog = DownloadFileProgressDialog(context!!)
                .cancelButton { presenter.onCancelDownloadingClicked(translation) }
                .show()
    }

    override fun hideTranslationDownloadProgress() {
        downloadProgressDialog?.dismiss()
        this.downloadProgressDialog = null
    }

    override fun updateTranslationDownloadProgress(downloadInfo: FileDownloadInfo) {
        downloadProgressDialog?.updateDownloadProgress(downloadInfo)
    }

    override fun showDeleteTranslationDialog(translation: WordByWordTranslationUIModel) {
        val deleteTranslationMessage = String.format(getString(R.string.remove_translation_message), translation.name)
        MaterialDialog(context!!).show {
            title(R.string.remove_translation_title)
            message(text = deleteTranslationMessage)
            positiveButton(R.string.btn_remove_title) { presenter.deleteTranslation(translation) }
            negativeButton(R.string.btn_cancel_title) { dismiss() }
        }
    }

    fun onBackPressed() {
        getGlobal<Navigator>().finish()
    }

}