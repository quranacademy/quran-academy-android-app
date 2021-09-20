package org.quranacademy.quran.mushaf.presentation.ui

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.MenuItem
import android.view.WindowManager
import android.view.animation.AccelerateInterpolator
import android.widget.CheckBox
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.list.listItems
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import kotlinx.android.synthetic.main.activity_mushaf.*
import kotlinx.android.synthetic.main.fragment_mushaf_page.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import me.aartikov.alligator.NavigationContext
import me.aartikov.alligator.Screen
import me.aartikov.alligator.ScreenResult
import org.quranacademy.quran.bookmarks.ui.BookmarkFoldersScreen
import org.quranacademy.quran.di.PrimitiveWrapper
import org.quranacademy.quran.di.bindSingleton
import org.quranacademy.quran.di.get
import org.quranacademy.quran.di.installModule
import org.quranacademy.quran.domain.models.*
import org.quranacademy.quran.domain.models.bounds.AyahBounds
import org.quranacademy.quran.mushaf.R
import org.quranacademy.quran.mushaf.di.AyahForHighlighting
import org.quranacademy.quran.mushaf.presentation.mvp.MushafPageCommandShell
import org.quranacademy.quran.mushaf.presentation.mvp.mushaf.MushafPresenter
import org.quranacademy.quran.mushaf.presentation.mvp.mushaf.MushafView
import org.quranacademy.quran.mushaf.presentation.ui.ayahtranslation.AyahTranslationsFragment
import org.quranacademy.quran.mushaf.presentation.ui.page.ToolbarPositionCalculator
import org.quranacademy.quran.mushaf.presentation.ui.page.ayahtranslationscontainer.AyahTranslationsPanel
import org.quranacademy.quran.mushaf.presentation.ui.page.ayahtranslationscontainer.TranslationsPanelManager
import org.quranacademy.quran.mushaf.presentation.ui.page.ayahtranslationscontainer.TranslationsPanelState
import org.quranacademy.quran.player.presentation.global.QuranPlayer
import org.quranacademy.quran.player.presentation.playeroptions.PlayerOptionsShell
import org.quranacademy.quran.presentation.extensions.enableStatusBarTranslucent
import org.quranacademy.quran.presentation.extensions.onPageScrolled
import org.quranacademy.quran.presentation.extensions.onPageSelected
import org.quranacademy.quran.presentation.extensions.showFullScreen
import org.quranacademy.quran.presentation.mvp.routing.screens.MushafScreen
import org.quranacademy.quran.presentation.textsettingspanel.TextSettingsPanel
import org.quranacademy.quran.presentation.ui.base.BaseThemedActivity
import org.quranacademy.quran.presentation.ui.dialogs.DownloadFileProgressDialog
import org.quranacademy.quran.presentation.ui.global.DisabledAnimationProvider
import org.quranacademy.quran.sharingdialog.SharingType
import org.quranacademy.quran.sharingdialog.TranslationsSharingScreen
import toothpick.Scope

class MushafActivity : BaseThemedActivity(), MushafView {

    override val layoutRes = R.layout.activity_mushaf

    override val scopeModuleInstaller: (Scope) -> Unit = {
        val mushafScreen = screenResolver.getScreen<MushafScreen>(this)
        it.installModule {
            bindSingleton<PlayerOptionsShell>()
            bindSingleton<PlayerOptionsShell>()
            bindSingleton<MushafPageCommandShell>()
            bind(PrimitiveWrapper::class.java)
                    .withName(AyahForHighlighting::class.java)
                    .toInstance(PrimitiveWrapper(mushafScreen.ayahForHighlighting))
        }
    }

    @InjectPresenter
    lateinit var presenter: MushafPresenter

    private val mushafPagesAdapter by lazy { MushafPagesAdapter(supportFragmentManager) }
    private lateinit var addBookmarkCheckbox: CheckBox
    private var imagesBundleDownloadingProgressDialog: DownloadFileProgressDialog? = null
    private var showDownloadImagesBundleDialog: MaterialDialog? = null
    private var isToolbarMenuInitialized = false
    private var isToolbarShowing = true
    private var currentPagePosition: Int = 0
    private var ayahToolbarPagePosition: Int = -1

    private val player by lazy { QuranPlayer(supportFragmentManager) }
    private val toolbarPositionCalculator by lazy {
        ToolbarPositionCalculator(this, ayahToolBar, pageContainer, pageLayout.mushafPageImage)
    }
    private val translationsPanelManager by lazy {
        TranslationsPanelManager(
                topTranslationsPanel,
                mushafSlidingPanel,
                ayahTranslationsPanel
        )
    }

    @ProvidePresenter
    fun providePresenter() = scope.get<MushafPresenter>()

    override fun onCreate(savedInstanceState: Bundle?) {
        enableStatusBarTranslucent(true)
        super.onCreate(savedInstanceState)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        launch {
            appearanceManager.getMushafThemeUpdates()
                    .collect { recreate() }
        }

        toolbar.setSubtitleTextAppearance(this, R.style.Toolbar_SubtitleText)
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp)
        toolbar.setNavigationOnClickListener { presenter.onBackPressed() }

        translationsPanelManager.onStateChanged {
            val isSwapButtonVisible = !translationsPanelManager.isBottom() ||
                    it != TranslationsPanelState.OPENED
            ayahTranslationsPanel.showSwapButton(isSwapButtonVisible)
            if (it == TranslationsPanelState.CLOSED) {
                presenter.onTranslationPanelClosed()
            }
            updateStatusBarViewVisibility()
        }

        initStatusBarView()
        ayahTranslationsPanel.onSwapClickListener = {
            translationsPanelManager.swap()
            ayahTranslationsPanel.post {
                updateStatusBarViewVisibility()
            }
        }

        ayahTranslationsPanel.onMenuItemClick = { itemId ->
            when (itemId) {
                R.id.openAppSettings -> presenter.onOpenSettingsScreenClicked()
                R.id.openTextSettings -> {
                    TextSettingsPanel().show(supportFragmentManager, "TextSettingsPanel")
                }
                R.id.copyAyah -> presenter.onShareAyahClicked(SharingType.COPYING)
                R.id.shareAyah -> presenter.onShareAyahClicked(SharingType.SHARING)
                R.id.playAyah -> presenter.onPlayAyahClicked()
            }
        }
        openPlayerFab.setOnClickListener { presenter.onOpenPlayerButtonClicked() }

        translationsPanelManager.init()
    }

    override fun onPause() {
        super.onPause()

        presenter.saveLastReadPosition()
    }

    override fun showImagesBundleDownloadSuggestion(isVisible: Boolean) {
        if (isVisible) {
            showDownloadImagesBundleDialog = MaterialDialog(this).show {
                title(R.string.download_images_bundle_title)
                message(R.string.download_images_bundle_message)
                positiveButton(R.string.btn_label_ok) { presenter.onDownloadImagesBundleClicked() }
                negativeButton(R.string.btn_cancel_title) { presenter.onDisableImagesBundleDownloadingSuggestionClicked() }
                cancelable(false)
            }
        } else {
            showDownloadImagesBundleDialog?.cancel()
        }
    }

    override fun showImagesBundleDownloadProgress(isVisible: Boolean) {
        if (isVisible) {
            this.imagesBundleDownloadingProgressDialog = DownloadFileProgressDialog(this)
                    .cancelButton { presenter.onCancelImagesBundleDownloadingClicked() }
                    .show()
        } else {
            imagesBundleDownloadingProgressDialog?.dismiss()
            this.imagesBundleDownloadingProgressDialog = null
        }

    }

    override fun updateImagesBundleDownloadProgress(downloadInfo: FileDownloadInfo) {
        imagesBundleDownloadingProgressDialog?.updateDownloadProgress(downloadInfo)
    }

    override fun showQuranPager() {
        mushafPager.adapter = mushafPagesAdapter
        mushafPager.onPageScrolled { position, _, positionOffsetPixels ->
            if (positionOffsetPixels == 0) {
                return@onPageScrolled
            }

            val ayahToolBarPos = ayahToolBar.getPosition()
            if (ayahToolBar.isShowing() && ayahToolBarPos != null) {
                when (position) {
                    //scroll to left page (i.e. next quran page)
                    ayahToolbarPagePosition -> {
                        ayahToolBarPos.xScroll = mushafPager.width.toFloat() - positionOffsetPixels
                        ayahToolBar.updatePosition(ayahToolBarPos)
                    }
                    //scroll to right page (i.e. prev quran page)
                    ayahToolbarPagePosition - 1 -> {
                        ayahToolBarPos.xScroll = -1f * positionOffsetPixels
                        ayahToolBar.updatePosition(ayahToolBarPos)
                    }
                    // toolbar totally off screen, should hide toolbar
                    else -> {
                        ayahToolBar.hideToolbar()
                        ayahToolbarPagePosition = -1
                        return@onPageScrolled
                    }
                }
            }
        }

        mushafPager.onPageSelected { position ->
            if (position != currentPagePosition) {
                //ayahToolBar.hideToolbar()
                val pageNumber = position + 1
                presenter.onPageChanged(pageNumber)
                currentPagePosition = position
            }
        }
    }

    override fun switchToPage(pageNumber: Int) {
        //т. к. позиция начинается с 0, а страница с 1, убираем единицу
        val realPagePosition = pageNumber - 1
        mushafPager.currentItem = realPagePosition
    }

    override fun showToolbar(isVisible: Boolean) {
        if (isToolbarShowing != isVisible) {
            toolbarContainer.animate()
                    .translationY(if (isVisible) 0f else -toolbarContainer.height.toFloat())
                    .setInterpolator(AccelerateInterpolator(2f))
                    .start()
            showFullScreen(isVisible)

            if (isVisible) openPlayerFab.show() else openPlayerFab.hide()

            isToolbarShowing = isVisible
        }
    }

    override fun showPageInfo(page: QuranPage) {
        if (!isToolbarMenuInitialized) {
            toolbar.inflateMenu(R.menu.mushaf_menu)
            toolbar.setOnMenuItemClickListener { onMenuItemClick(it) }
            toolbar.menu.localizeMenu()

            val addBookmarkItem = toolbar.menu.findItem(R.id.action_add_bookmark)
            addBookmarkCheckbox = addBookmarkItem.actionView.findViewById(R.id.addBookmarkCheckbox)
            isToolbarMenuInitialized = true
        }

        toolbar.title = page.surahName
        toolbar.subtitle = getString(R.string.juz_title, page.juzNumber)
        addBookmarkCheckbox.isChecked = page.isBookmarked
        addBookmarkCheckbox.setOnCheckedChangeListener { _, isChecked ->
            presenter.onBookmarkPage(isChecked)
        }
    }

    override fun showAyahToolbar(ayahDetails: AyahDetails, ayahBounds: List<AyahBounds>) {
        ayahToolBar.setBookmarked(ayahDetails.isBookmarked)
        val toolbarPosition = toolbarPositionCalculator.getToolBarPosition(
                ayahBounds,
                translationsPanelManager.isShowing(),
                translationsPanelManager.isBottom()
        )
        ayahToolBar.getPosition()?.let { currentToolbarPosition ->
            toolbarPosition.yScroll = currentToolbarPosition.yScroll
        }
        ayahToolBar.updatePosition(toolbarPosition)

        var isAyahBookmarked = ayahDetails.isBookmarked
        ayahToolBar.itemSelectedListener = {
            when (it.itemId) {
                R.id.cab_bookmark_ayah -> {
                    isAyahBookmarked = !isAyahBookmarked
                    ayahToolBar.setBookmarked(isAyahBookmarked)
                    presenter.onBookmarkAyahClicked(ayahDetails.id, isAyahBookmarked)
                }
                R.id.cab_show_ayah_details -> presenter.onShowAyahTranslationsClicked()
                R.id.cab_copy_ayah -> presenter.onShareAyahClicked(SharingType.COPYING)
                R.id.cab_share_ayah -> presenter.onShareAyahClicked(SharingType.SHARING)
                R.id.cab_play_selected_ayahs -> presenter.onPlayAyahClicked()
            }
        }
        ayahToolBar.showToolbar()
        ayahToolbarPagePosition = currentPagePosition
    }

    override fun hideAyahToolbar() {
        ayahToolBar.hideToolbar()
    }

    override fun showAyahTranslations(ayahIds: List<AyahId>) {
        val translationsFragment = AyahTranslationsFragment.newInstance(ayahIds)
        supportFragmentManager
                .beginTransaction()
                .add(R.id.ayahTranslationsContainer, translationsFragment, "AyahTranslationsFragment")
                .commitNow()
        if (!translationsPanelManager.isShowing() && !translationsPanelManager.isBottom()) {
            //return bottom position if panel has been hidden before
            ayahTranslationsPanel.setPanelPosition(AyahTranslationsPanel.PanelPosition.BOTTOM)
            translationsPanelManager.swap()
        }
        translationsPanelManager.show()
    }

    override fun closeTranslationPanel() {
        translationsPanelManager.hide()
    }

    override fun showPlayerOptionsPanel(startAyah: AyahId, endAyah: AyahId) {
        player.showPlayerOptionsPanel(startAyah, endAyah)
    }

    override fun hidePlayerOptionsPanel() = player.hidePlayerOptionsPanel()

    override fun showPlayerControlPanel() = player.showPlayerControlPanel()

    override fun hidePlayerControlPanel() = player.hidePlayerControlPanel()

    override fun showMushafThemeSelectingDialog() {
        val themeTitles = resources.getStringArray(R.array.app_theme_titles)
        val mushafThemeValues = AppTheme.values()
        MaterialDialog(this).show {
            listItems(items = themeTitles.toList()) { _, index, _ ->
                presenter.onThemeSelected(mushafThemeValues[index])
            }
        }
    }

    override fun setHorizontalMode(isEnabled: Boolean) {
        toolbar.menu.findItem(R.id.action_horizontal_mode).isChecked = isEnabled
        requestedOrientation = if (isEnabled) {
            ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        } else {
            ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
        }
    }

    override fun onBackPressed() = presenter.onBackPressed()

    // Used to sync toolbar with page's SV (landscape non-tablet mode)
    fun onPageVerticalScroll(scrollY: Int) {
        ayahToolBar.getPosition()?.let { position ->
            position.yScroll = 0f - scrollY
            ayahToolBar.updatePosition(position)
        }
    }

    override fun createNavigationContext(): NavigationContext.Builder {
        return super.createNavigationContext()
                .transitionAnimationProvider(DisabledAnimationProvider())
    }

    override fun onScreenResult(screenClass: Class<out Screen>, result: ScreenResult?) {
        when (result) {
            is BookmarkFoldersScreen.Result -> {
                presenter.onAyahBookmarked(result.ayahId)
            }
            else -> {
                player.onScreenResult(screenClass, result)
            }
        }
    }

    private fun updateStatusBarViewVisibility() {
        val state = translationsPanelManager.getState()
        val isBottom = translationsPanelManager.isBottom()
        val isExpanded = state == TranslationsPanelState.OPENED
        val isPanelVisible = translationsPanelManager.isShowing()
        val isVisible = isPanelVisible && (isBottom && isExpanded || !isBottom)
        val color = if (isVisible) R.color.hqa_dark_grey_100 else R.color.transparent
        statusBar.setBackgroundColor(ContextCompat.getColor(this, color))
    }

    private fun initStatusBarView() {
        val statusBarHeightId = resources.getIdentifier("status_bar_height", "dimen", "android")
        val statusBarHeight = resources.getDimensionPixelSize(statusBarHeightId)
        statusBar.layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                statusBarHeight
        )
    }

    private fun onMenuItemClick(menuItem: MenuItem): Boolean {
        when (menuItem.itemId) {
            R.id.action_change_mushaf_theme -> {
                presenter.onChangeMushafThemeClicked()
            }
            R.id.action_switch_reader_mode -> {
                presenter.onSwitchReadModeClicked()
            }
            //R.id.action_mushaf_type_selection -> {
            //    presenter.onOpenMushafTypeSelectionScreenClicked()
            //}
            R.id.action_horizontal_mode -> {
                presenter.onEnableHorizontalModeClicked(menuItem.isChecked)
            }
        }
        return true
    }

}