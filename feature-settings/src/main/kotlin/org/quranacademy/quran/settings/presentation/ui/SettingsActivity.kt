package org.quranacademy.quran.settings.presentation.ui

import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.list.listItems
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import kotlinx.android.synthetic.main.activity_preferences.*
import kotlinx.android.synthetic.main.dialog_storage_selection.view.*
import org.quranacademy.quran.di.get
import org.quranacademy.quran.domain.models.AppTheme
import org.quranacademy.quran.domain.models.FileDownloadInfo
import org.quranacademy.quran.domain.models.MushafPageType
import org.quranacademy.quran.presentation.extensions.bytes2String
import org.quranacademy.quran.presentation.extensions.inflate
import org.quranacademy.quran.presentation.extensions.visible
import org.quranacademy.quran.presentation.mvp.routing.screens.*
import org.quranacademy.quran.presentation.ui.base.BaseThemedActivity
import org.quranacademy.quran.presentation.ui.dialogs.DownloadFileProgressDialog
import org.quranacademy.quran.settings.R
import org.quranacademy.quran.settings.domain.StoragesInfo
import org.quranacademy.quran.settings.presentation.mvp.AppSettingsPresenter
import org.quranacademy.quran.settings.presentation.mvp.AppSettingsView
import org.quranacademy.quran.ui.preferences.io.StandardUserInputModule

class SettingsActivity : BaseThemedActivity(), AppSettingsView {

    override val layoutRes = R.layout.activity_preferences

    @InjectPresenter
    lateinit var presenter: AppSettingsPresenter

    @ProvidePresenter
    fun providePresenter() = scope.get<AppSettingsPresenter>()

    private val toolbar: Toolbar by lazy { findViewById<Toolbar>(R.id.toolbar) }
    private var imagesBundleDownloadingProgressDialog: DownloadFileProgressDialog? = null
    private var appDataTransferProgressDialog: DownloadFileProgressDialog? = null

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        toolbar.title = getString(R.string.settings)
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp)
        toolbar.setNavigationOnClickListener { onBackPressed() }

        //prefs(prefsView, storage) {
        //    category("My category") {
        //        preference {
        //            title("My pref item")
        //            icon(R.drawable.my_icon)
        //            onClick {
        //                //my action
        //            }
        //        }
        //    }
        //}

        preferenceScreen.storageModule = PreferencesStorageModule(this)
        preferenceScreen.userInputModule = StandardUserInputModule(this)

        prefTranslationsScreen.setOnClickListener {
            navigator.goForward(TranslationsManagerScreen(false))
        }

        prefTafseersScreen.setOnClickListener {
            navigator.goForward(TranslationsManagerScreen(true))
        }

        prefTranslationsOrderingScreen.setOnClickListener {
            navigator.goForward(TranslationsOrderScreen())
        }

        prefWordByWordTranslationsScreen.setOnClickListener {
            navigator.goForward(WordByWordTranslationsManagerScreen())
        }

        //prefMushafPageType.setOnClickListener {
        //    navigator.goForward(MushafPageTypeSelectionScreen())
        //}

        prefAppLanguage.setOnClickListener {
            navigator.goForward(LanguagesManagerScreen())
        }

        prefHistoryItemsSize.setOnClickListener {
            presenter.onSelectReadingHistorySizeClicked()
        }
        prefDownloadAllMushafImages.setOnClickListener {
            presenter.onDownloadAllMushafImagesClicked()
        }
        prefFilesLocation.setOnClickListener {
            presenter.onChooseFilesLocationClicked()
        }

        val themeTitles = resources.getStringArray(R.array.app_theme_titles)
        val themeValues = AppTheme.values().map { it.codeName }.toTypedArray()
        val currentTheme = appearanceManager.getCurrentAppTheme()
        prefAppTheme.setData(themeTitles, themeValues, currentTheme.codeName)

        val mushafThemeTitles = resources.getStringArray(R.array.app_theme_titles)
        val mushafThemeValues = AppTheme.values().map { it.codeName }.toTypedArray()
        val currentMushafTheme = appearanceManager.getCurrentMushafTheme()
        prefMushafTheme.setData(mushafThemeTitles, mushafThemeValues, currentMushafTheme.codeName)


        prefQuranTextSettings.setOnClickListener {
            navigator.goForward(QuranTextSettingsScreen())
        }
    }

    override fun showTranslationUpdatesCount(count: Int) {
        if (count > 0) {
            prefTranslationsScreen.setValue(count.toString())
        } else {
            prefTranslationsScreen.hideLabel()
        }
    }

    override fun showTafseerUpdatesCount(count: Int) {
        if (count > 0) {
            prefTafseersScreen.setValue(count.toString())
        } else {
            prefTafseersScreen.hideLabel()
        }
    }

    override fun showWordByWordTranslationUpdatesCount(count: Int) {
        if (count > 0) {
            prefWordByWordTranslationsScreen.setValue(count.toString())
        } else {
            prefWordByWordTranslationsScreen.hideLabel()
        }
    }

    override fun showReadingHistorySize(size: Int) {
        prefHistoryItemsSize.setSummary(size.toString())
    }

    override fun showMushafPagesType(type: MushafPageType) {
        val mushafTypeName = when (type) {
            MushafPageType.MADANI_NEW -> R.string.madani_new_title
            else -> R.string.madani_title
        }
        //prefMushafPageType.setSummary(mushafTypeName)
    }

    override fun hideImagesBundleDownloadingItem() {
        prefDownloadAllMushafImages.visible(false)
    }

    override fun showReadingHistorySizeSelectionDialog() {
        MaterialDialog(this).show {
            title(R.string.prefs_reading_history_size_title)
            val sizeTitles = arrayOf(3, 5, 7, 10).map { it.toString() }
            listItems(items = sizeTitles) { _, index, _ ->
                presenter.onReadingHistorySizeSelected(sizeTitles[index].toInt())
            }
        }
    }

    override fun showImagesBundleDownloadProgress(isVisible: Boolean) {
        var progressDialog = this.imagesBundleDownloadingProgressDialog
        if (isVisible && progressDialog == null) {
            progressDialog = DownloadFileProgressDialog(this)
                    .cancelButton { presenter.onCancelImagesBundleDownloadingClicked() }
                    .show()
            this.imagesBundleDownloadingProgressDialog = progressDialog
        } else if (!isVisible && progressDialog != null) {
            progressDialog.dismiss()
            this.imagesBundleDownloadingProgressDialog = null
        }
    }

    override fun updateImagesBundleDownloadProgress(downloadInfo: FileDownloadInfo) {
        imagesBundleDownloadingProgressDialog?.updateDownloadProgress(downloadInfo)
    }

    override fun showStorageChoosingPref() {
        prefFilesLocation.visible(true)
    }

    override fun showStorageChoosingDialog(storages: StoragesInfo) {
        MaterialDialog(this).show {
            val contentView = context.inflate(R.layout.dialog_storage_selection)
            contentView.appUsedSpace.text = getString(
                    R.string.user_app_space_space_info,
                    storages.usedAppSpace.bytes2String()
            )
            contentView.storagesList.adapter = StoragesAdapter(storages) {
                dismiss()
                presenter.onStorageSelected(it)
            }
            customView(view = contentView)
        }
    }

    override fun showAppDataTransferProgress(isVisible: Boolean) {
        val progressDialog = this.appDataTransferProgressDialog
        if (isVisible && progressDialog == null) {
            this.appDataTransferProgressDialog = DownloadFileProgressDialog(this)
                    .indeterminate(true)
                    .setTitle(getString(R.string.copying_app_files_title))
                    .updateMessage("")
                    .show()
        } else if (!isVisible && progressDialog != null) {
            progressDialog.dismiss()
            this.appDataTransferProgressDialog = null
        }
    }

}
