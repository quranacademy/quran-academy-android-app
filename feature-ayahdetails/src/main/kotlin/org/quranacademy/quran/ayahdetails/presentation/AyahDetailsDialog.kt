package org.quranacademy.quran.ayahdetails.presentation

import android.app.Dialog
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.widget.CheckBox
import com.afollestad.assent.Permission
import com.afollestad.assent.askForPermissions
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import kotlinx.android.synthetic.main.dialog_ayah_details.view.*
import me.aartikov.alligator.Screen
import me.aartikov.alligator.ScreenResult
import org.quranacademy.quran.ayahdetails.R
import org.quranacademy.quran.core.permissions.createHQAPermissionsRationale
import org.quranacademy.quran.core.permissions.isPermissionBlockedFromAsking
import org.quranacademy.quran.core.permissions.showGrantPermissionFromSettingsDialog
import org.quranacademy.quran.di.get
import org.quranacademy.quran.di.installModule
import org.quranacademy.quran.domain.models.AyahDetails
import org.quranacademy.quran.domain.models.AyahId
import org.quranacademy.quran.presentation.extensions.inflate
import org.quranacademy.quran.presentation.extensions.isHorizontalMode
import org.quranacademy.quran.presentation.extensions.setSize
import org.quranacademy.quran.presentation.extensions.visible
import org.quranacademy.quran.presentation.mvp.routing.screens.AyahDetailsScreen
import org.quranacademy.quran.presentation.ui.base.BaseDialogFragment
import org.quranacademy.quran.presentation.ui.global.getTypeface
import org.quranacademy.quran.presentation.ui.languagesystem.utils.localizeMenu
import org.quranacademy.quran.sharingdialog.SharingType
import org.quranacademy.quran.sharingdialog.TranslationsSharingScreen
import toothpick.Scope

class AyahDetailsDialog : BaseDialogFragment(), AyahDetailsView {

    @InjectPresenter
    lateinit var presenter: AyahDetailsPresenter

    private lateinit var dialogContentView: View
    private lateinit var addBookmarkCheckbox: CheckBox

    override val scopeModuleInstaller: (Scope) -> Unit = { scope ->
        val screen = screenResolver.getScreen<AyahDetailsScreen>(this)
        val ayahId = screen.ayahId
        scope.installModule {
            bind(AyahId::class.java).toInstance(ayahId)
        }
    }

    @ProvidePresenter
    fun providePresenter() = scope.get<AyahDetailsPresenter>()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        dialogContentView = inflate(R.layout.dialog_ayah_details)
        dialogContentView.toolbar.inflateMenu(R.menu.ayah_details_menu)
        dialogContentView.toolbar.setOnMenuItemClickListener { menuItem -> onMenuItemSelected(menuItem) }
        dialogContentView.toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp)
        dialogContentView.toolbar.setNavigationOnClickListener { dismiss() }
        dialogContentView.toolbar.menu.localizeMenu(requireContext())

        val addBookmarkItem = dialogContentView.toolbar.menu.findItem(R.id.action_add_bookmark)
        addBookmarkCheckbox = addBookmarkItem.actionView.findViewById(R.id.addBookmarkCheckbox)

        val translationTypeface = appearanceManager.getTranslationFont().getTypeface(requireContext())
        dialogContentView.translationsContainer.setTranslationTypeface(translationTypeface)
        dialogContentView.translationsContainer.setTranslationTextSize(appearanceManager.getTranslationTextSize())
        dialogContentView.translationsContainer.setTextCenteringEnabled(appearanceManager.isTranslationTextCenteringEnabled())

        return MaterialDialog(requireContext()).show {
            if (!context.isHorizontalMode()) {
                setSize(widthInPercents = 0.95f, heightInPercents = 0.25f)
            } else {
                setSize(widthInPercents = 0.90f, heightInPercents = 0.5f)
            }
            customView(
                    view = dialogContentView,
                    noVerticalPadding = true
            )
        }
    }

    override fun showProgressLayout(isVisible: Boolean) {
        dialogContentView.progressLayout.visible(isVisible)
    }

    override fun showAyahDetails(ayah: AyahDetails) {
        dialogContentView.toolbar.title = "${ayah.surahNumber}:${ayah.ayahNumber}"
        addBookmarkCheckbox.isChecked = ayah.isBookmarked
        addBookmarkCheckbox.setOnCheckedChangeListener { _, isChecked ->
            presenter.onBookmarkAyahClicked(isChecked)
        }

        dialogContentView.translationsContainer.visible(ayah.translations.isNotEmpty())
        dialogContentView.translationsContainer.setAyahTranslations(ayah.translations)
        dialogContentView.post {
            calculateDialogSize()
        }
    }

    override fun showTranslationsListEmptyLabel() {
        dialogContentView.translationsNotFoundLabel.visible(true)
        dialogContentView.post {
            calculateDialogSize()
        }
    }

    private fun onMenuItemSelected(menuItem: MenuItem?): Boolean {
        when (menuItem!!.itemId) {
            R.id.action_copy_ayah -> presenter.onShareAyahClicked(SharingType.COPYING)
            R.id.action_share_ayah -> presenter.onShareAyahClicked(SharingType.SHARING)
            R.id.action_play_ayah -> startAyahPlayback()
        }
        return true
    }

    private fun startAyahPlayback() {
        val rationaleHandler = createHQAPermissionsRationale {
            onPermission(Permission.READ_EXTERNAL_STORAGE, R.string.external_permission_not_needed_message)
        }

        askForPermissions(Permission.READ_EXTERNAL_STORAGE, rationaleHandler = rationaleHandler) { result ->
            val grantResult = result.grantResults.first()
            if (grantResult == PackageManager.PERMISSION_GRANTED) {
                presenter.onPlayAyahClicked()
            } else {
                if (isPermissionBlockedFromAsking(Permission.READ_EXTERNAL_STORAGE)) {
                    showGrantPermissionFromSettingsDialog(requireContext())
                } else {
                    startAyahPlayback()
                }
            }
        }
    }

    private fun calculateDialogSize() {
        dialog?.window?.let { dialogWindow ->
            val layoutParams = WindowManager.LayoutParams()
            layoutParams.copyFrom(dialogWindow.attributes)
            layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT
            dialogWindow.attributes = layoutParams
            dialogContentView.requestLayout()
        }
    }

}