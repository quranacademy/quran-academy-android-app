package org.quranacademy.quran.splash.presentation.ui.splash

import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.list.listItemsSingleChoice
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import kotlinx.android.synthetic.main.activity_splash.*
import org.quranacademy.quran.di.get
import org.quranacademy.quran.domain.models.Language
import org.quranacademy.quran.presentation.extensions.setSize
import org.quranacademy.quran.presentation.extensions.visible
import org.quranacademy.quran.presentation.ui.base.BaseActivity
import org.quranacademy.quran.splash.R
import org.quranacademy.quran.splash.domain.splash.QuranDataDownloadingProgress
import org.quranacademy.quran.splash.presentation.mvp.splash.SplashPresenter
import org.quranacademy.quran.splash.presentation.mvp.splash.SplashView

/**
 * Данная активити не унаследована от BaseThemedActivity по причине того, что она не похожа на другие активити
 * В BaseThemedActivity отслеживается смена языка, и это вызывает баг с пересозданием этой Activity
 * и повторным показом диалога с языками
 */
class SplashActivity : BaseActivity(), SplashView {

    override val layoutRes: Int = R.layout.activity_splash

    @InjectPresenter
    lateinit var presenter: SplashPresenter

    @ProvidePresenter
    fun providePresenter() = scope.get<SplashPresenter>()

    private val systemLanguageManager by lazy { scope.get<SystemLanguageManager>() }

    override fun showLanguagesDialog(languages: List<Language>) {
        val languagesNames = languages.map { "${it.name} (${it.englishName})" }.toTypedArray().toList()
        MaterialDialog(this).show {
            setSize(widthInPercents = 0.9f)
            title(text = systemLanguageManager.getString(R.string.select_language_title))
            cancelable(false)
            listItemsSingleChoice(items = languagesNames) { _, index, _ ->
                val selectedLanguage = languages[index]
                languageManager.setAppLanguage(selectedLanguage.code)
                presenter.onLanguageSelected(selectedLanguage)
            }
            positiveButton(R.string.btn_label_ok)
        }
    }

    override fun showDownloadProgress(isVisible: Boolean) {
        statusProgress.visible(isVisible)
        statusLabel.visible(isVisible)
    }

    override fun showDownloadProgress(progress: QuranDataDownloadingProgress) {
        statusProgress.isIndeterminate = progress.isIndeterminate
        statusProgress.max = progress.maxProgress.toInt()
        statusProgress.progress = progress.currentProgress.toInt()
        statusLabel.text = progress.text
    }

    override fun showCheckNetworkDialog() {
        MaterialDialog(this).show {
            title(text = systemLanguageManager.getString(R.string.check_network_dialog_message))
            message(text = systemLanguageManager.getString(R.string.check_network_dialog_title))
            positiveButton(text = systemLanguageManager.getString(R.string.btn_retry_label)) {
                presenter.onRetryLoadLanguagesListClicked()
            }
            negativeButton(text = systemLanguageManager.getString(R.string.btn_exit_label)) {
                presenter.onExitButtonClicked()
            }
            cancelable(false)
        }
    }

    override fun showPreparingProgress(isVisible: Boolean) {
        statusProgress.visible(isVisible)
        statusProgress.isIndeterminate = true
    }

    override fun showMessage(message: String) {
        statusLabel.visible(true)
        statusLabel.text = message
    }

    override fun onBackPressed() {
        //disable on back pressed handling
    }

    override fun hideStatusMessage() {
        statusLabel.visible(false)
    }

}