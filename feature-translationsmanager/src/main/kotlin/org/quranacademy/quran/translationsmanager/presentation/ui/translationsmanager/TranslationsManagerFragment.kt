package org.quranacademy.quran.translationsmanager.presentation.ui.translationsmanager

import android.os.Bundle
import android.view.View
import android.widget.Button
import com.afollestad.materialdialogs.MaterialDialog
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import kotlinx.android.synthetic.main.fragment_translations.*
import me.aartikov.alligator.Navigator
import org.quranacademy.quran.di.bindPrimitive
import org.quranacademy.quran.di.get
import org.quranacademy.quran.di.getGlobal
import org.quranacademy.quran.di.installModule
import org.quranacademy.quran.domain.models.FileDownloadInfo
import org.quranacademy.quran.presentation.extensions.visible
import org.quranacademy.quran.presentation.ui.base.BaseFragment
import org.quranacademy.quran.translationsmanager.R
import org.quranacademy.quran.translationsmanager.di.IsInitialSetupMode
import org.quranacademy.quran.translationsmanager.di.IsTafseers
import org.quranacademy.quran.translationsmanager.domain.translations.TranslationUIModel
import org.quranacademy.quran.translationsmanager.presentation.mvp.translationsmanager.TranslationsManagerPresenter
import org.quranacademy.quran.translationsmanager.presentation.mvp.translationsmanager.TranslationsManagerView
import org.quranacademy.quran.translationsmanager.presentation.ui.translationsmanager.adapter.TranslationsAdapter
import toothpick.Scope

class TranslationsManagerFragment : BaseFragment(), TranslationsManagerView {

    companion object {
        private const val IS_TAFSEER_KEY = "is_tafseer"

        // Данный фрагмент используется в двух местах:
        // 1. Диалог, показывающийся при начальной настройке
        // 2. Экоан управления переводами в настройка
        //
        // В первом случае внешний вид немного отличается, поэтому мы используем данный флаг
        private const val IS_INITIAL_SETUP_MODE = "is_initial_setup_mode"

        fun newInstance(isTafseer: Boolean, isInitialSetupMode: Boolean): TranslationsManagerFragment {
            val fragment = TranslationsManagerFragment()
            val args = Bundle()
            args.putBoolean(IS_TAFSEER_KEY, isTafseer)
            args.putBoolean(IS_INITIAL_SETUP_MODE, isInitialSetupMode)
            fragment.arguments = args
            return fragment
        }
    }

    override val layoutRes: Int = R.layout.fragment_translations

    private val translationsAdapter by lazy {
        TranslationsAdapter(
                context = context!!,
                downloadClickListener = { presenter.onDownloadTranslationClicked(it) },
                onCancelDownloadingClicked = { presenter.onCancelDownloadingClicked(it) },
                updateClickListener = { presenter.onUpdateTranslationClicked(it) },
                removeClickListener = { presenter.onDeleteTranslationClicked(it) },
                enableClickListener = { translation, isEnabled ->
                    presenter.onEnableTranslationClicked(translation, isEnabled)
                }
        )
    }
    private val retryButton: Button by lazy { view!!.findViewById<Button>(R.id.retryButton) }

    override val scopeModuleInstaller: (Scope) -> Unit = { scope ->
        val isTafseer = arguments!!.getBoolean(IS_TAFSEER_KEY)
        val isInitialSetupMode = arguments!!.getBoolean(IS_INITIAL_SETUP_MODE)
        scope.installModule {
            bindPrimitive(IsTafseers::class, isTafseer)
            bindPrimitive(IsInitialSetupMode::class, isInitialSetupMode)
        }
    }

    @InjectPresenter
    lateinit var presenter: TranslationsManagerPresenter

    @ProvidePresenter
    fun providePresenter() = scope.get<TranslationsManagerPresenter>()

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

    override fun showNoNetworkLayout(isVisible: Boolean) {
        noNetworkConnectionLayout.visible(isVisible)
    }

    override fun showTranslations(translations: List<TranslationUIModel>, isInitialSetupMode: Boolean) {
        translationsRefreshLayout.isEnabled = !isInitialSetupMode
        translationsAdapter.setData(
                translations = translations,
                splitByCategory = !isInitialSetupMode
        )
    }

    override fun updateTranslations(translations: List<TranslationUIModel>, isInitialSetupMode: Boolean) {
        translationsAdapter.updateData(
                translations = translations,
                splitByCategory = !isInitialSetupMode
        )
    }

    override fun updateTranslationDownloadProgress(
            translation: TranslationUIModel,
            downloadInfo: FileDownloadInfo?
    ) {
        translationsAdapter.updateDownloadProgress(translation, downloadInfo)
    }

    override fun showDeleteTranslationDialog(translation: TranslationUIModel) {
        val deleteTranslationMessage = String.format(getString(R.string.remove_translation_message), translation.name)

        MaterialDialog(context!!).show {
            title(R.string.remove_translation_title)
            message(text = deleteTranslationMessage)
            positiveButton(R.string.btn_label_ok) { presenter.deleteTranslation(translation) }
            negativeButton(R.string.btn_cancel_title)
        }
    }

    fun onBackPressed() {
        getGlobal<Navigator>().finish()
    }

}