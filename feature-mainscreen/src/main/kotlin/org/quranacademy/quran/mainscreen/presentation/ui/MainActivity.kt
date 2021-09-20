package org.quranacademy.quran.mainscreen.presentation.ui

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.Toolbar
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.utils.MDUtil
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.mikepenz.materialdrawer.DrawerBuilder
import com.mikepenz.materialdrawer.holder.DimenHolder
import com.mikepenz.materialdrawer.model.DividerDrawerItem
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem
import com.tapadoo.alerter.Alerter
import kotlinx.android.synthetic.main.activity_main.*
import org.quranacademy.quran.di.get
import org.quranacademy.quran.mainscreen.R
import org.quranacademy.quran.mainscreen.presentation.mvp.MainPresenter
import org.quranacademy.quran.mainscreen.presentation.mvp.MainView
import org.quranacademy.quran.mainscreen.presentation.ui.bookmarks.BookmarksFragment
import org.quranacademy.quran.mainscreen.presentation.ui.surahslist.SurahsFragment
import org.quranacademy.quran.player.presentation.global.QuranPlayer
import org.quranacademy.quran.presentation.extensions.getThemeColor
import org.quranacademy.quran.presentation.extensions.setSize
import org.quranacademy.quran.presentation.ui.base.BaseThemedActivity
import org.quranacademy.quran.radio.presentation.control.RadioControlFragment

class MainActivity : BaseThemedActivity(), MainView, ToolbarOwner {

    override val layoutRes = R.layout.activity_main

    @InjectPresenter
    lateinit var presenter: MainPresenter

    @ProvidePresenter
    fun providePresenter() = scope.get<MainPresenter>()

    private val toolbar: Toolbar by lazy { findViewById<Toolbar>(R.id.toolbar) }
    private val player by lazy { QuranPlayer(supportFragmentManager) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        toolbar.setTitle(R.string.app_name)
        toolbar.inflateMenu(R.menu.main_menu)
        toolbar.setOnMenuItemClickListener { onMenuItemClick(it.itemId) }
        toolbar.menu.localizeMenu()

        setupViewPager()
        setupNavigationDrawer()
        setupRadioControl()

        openPlayerFab.setOnClickListener {
            presenter.onOpenPlayerButtonClick()
        }

        player.onPlayerControlVisibilityChanged = { isVisible ->
            presenter.onPlayerControlVisibilityChanged(isVisible)
        }
    }

    override fun provideToolbar(): Toolbar = toolbar

    override fun showTranslationsCopyrightDialog() {
        MaterialDialog(this).show {
            setSize(widthInPercents = 0.9f)
            title(R.string.translations_copyright)
            val text = MDUtil.resolveString(
                    materialDialog = this,
                    res = R.string.translations_copyright_title_message,
                    html = true
            )
            message(text = text)
            cancelable(false)
            positiveButton(R.string.btn_label_ok) {
                presenter.onTranslationsCopyrightAcceptedClicked()
            }
        }
    }

    override fun showTranslationsUpdatesAvailable(isVisible: Boolean) {
        if (isVisible) {
            Alerter.create(this)
                    .setTitle(R.string.translation_update_notification_title)
                    .setText(R.string.translation_update_notification_details)
                    .setBackgroundColorRes(R.color.hqa_blue_50)
                    .setOnClickListener(View.OnClickListener { presenter.onTranslationUpdatesNotificationClicked() })
                    .show()
        } else {
            Alerter.hide()
        }
    }

    override fun showPlayerButton(isShowing: Boolean) {
        if (isShowing) {
            openPlayerFab.show()
        } else {
            openPlayerFab.hide()
        }
    }

    override fun showPlayerControlPanel() = player.showPlayerControlPanel()

    override fun hidePlayerControlPanel() = player.hidePlayerControlPanel()

    private fun onMenuItemClick(itemId: Int): Boolean {
        when (itemId) {
            R.id.action_search -> presenter.onOpenSearchScreenClicked()
            R.id.action_open_bookmark -> presenter.onOpenBookmarkClicked()
            R.id.action_jump_to_ayah -> presenter.onJumpToAyahClicked()
        }
        return true
    }

    private fun setupNavigationDrawer() {
        fun getIcon(iconResId: Int): Drawable {
            //drawable кешируются для всего приложения, и чтобы избежать
            //применения цвета на глобальном уровне, используем mutate
            return getDrawable(iconResId)!!.mutate().apply {
                setTint(getThemeColor(R.attr.material_drawer_primary_icon))
            }
        }

        DrawerBuilder()
                .withActivity(this)
                .withToolbar(toolbar)
                .withSelectedItem(-1)
                .withTranslucentStatusBar(false)
                .withHeader(R.layout.view_drawer_header)
                .withHeaderHeight(DimenHolder.fromDp(165))
                .addDrawerItems(
                        PrimaryDrawerItem()
                                .withIdentifier(ID_JUMP_TO_AYAH)
                                .withName(R.string.jump_to_ayah)
                                .withIcon(getIcon(R.drawable.ic_jump_to_ayah_white_24dp))
                                .withSelectable(false),
                        PrimaryDrawerItem()
                                .withIdentifier(ID_TAJWEED_RULES)
                                .withName(R.string.tajweed_rules)
                                .withIcon(getIcon(R.drawable.ic_quran_24dp))
                                .withSelectable(false),
                        PrimaryDrawerItem()
                                .withIdentifier(ID_QURAN_MEMORIZATION)
                                .withName(R.string.quran_memorization)
                                .withIcon(getIcon(R.drawable.ic_quran_memorization_24dp))
                                .withSelectable(false),
                        PrimaryDrawerItem()
                                .withIdentifier(ID_AUDIO_MANAGER)
                                .withName(R.string.audio_manager)
                                .withIcon(getIcon(R.drawable.ic_audio_manager_white_24dp))
                                .withSelectable(false),
                        PrimaryDrawerItem()
                                .withIdentifier(ID_SETTINGS)
                                .withName(R.string.settings)
                                .withIcon(getIcon(R.drawable.ic_settings_white_24dp))
                                .withSelectable(false),
                        DividerDrawerItem(),
                        //PrimaryDrawerItem()
                        //        .withIdentifier(ID_RADIO)
                        //        .withName(R.string.quran_academy_radio)
                        //        .withIcon(getIcon(R.drawable.ic_radio_white_24dp))
                        //        .withSelectable(false),
                        PrimaryDrawerItem()
                                .withIdentifier(ID_LEAVE_FEEDBACK)
                                .withName(R.string.leave_feedback_title)
                                .withIcon(getIcon(R.drawable.ic_message_white_24dp))
                                .withSelectable(false),
                        PrimaryDrawerItem()
                                .withIdentifier(ID_SHARE_APP)
                                .withName(R.string.share_app)
                                .withIcon(getIcon(R.drawable.ic_share_white_24dp))
                                .withSelectable(false),
                        PrimaryDrawerItem()
                                .withIdentifier(ID_RATE_APP)
                                .withName(R.string.rate_app)
                                .withIcon(getIcon(R.drawable.ic_rate_app_white_24dp))
                                .withSelectable(false),
                        PrimaryDrawerItem()
                                .withIdentifier(ID_COPYRIGHT_INFO)
                                .withName(R.string.translations_copyright)
                                .withIcon(getIcon(R.drawable.ic_copyright_gray_24dp))
                                .withSelectable(false)
                )

                .withOnDrawerItemClickListener { _, _, drawerItem ->
                    when (drawerItem.identifier) {
                        ID_JUMP_TO_AYAH -> presenter.onJumpToAyahClicked()
                        ID_TAJWEED_RULES -> presenter.onOpenTajweedRulesClicked()
                        ID_QURAN_MEMORIZATION -> presenter.onOpenQuranMemorizationClicked()
                        ID_AUDIO_MANAGER -> presenter.onAudioManagerClicked()
                        ID_SETTINGS -> presenter.onOpenSettingsClicked()
                        ID_RADIO -> presenter.onOpenRadioCleacked()
                        ID_LEAVE_FEEDBACK -> presenter.onLeaveFeedbackClicked()
                        ID_SHARE_APP -> presenter.onShareAppClicked()
                        ID_RATE_APP -> presenter.onRateAppClicked()
                        ID_COPYRIGHT_INFO -> presenter.onShowCopyrightInfoClicked()
                    }
                    return@withOnDrawerItemClickListener false
                }
                .build()
    }

    private fun setupViewPager() {
        val mainPagerAdapter = MainPagerAdapter(supportFragmentManager)
        mainPagerAdapter.addFragment(SurahsFragment.newInstance(), getString(R.string.surahs))
        mainPagerAdapter.addFragment(BookmarksFragment.newInstance(), getString(R.string.bookmarks))
        mainPager.adapter = mainPagerAdapter
        tabs.setupWithViewPager(mainPager)
    }

    private fun setupRadioControl() {
        val radioControl = RadioControlFragment.newInstance()
        supportFragmentManager
                .beginTransaction()
                .replace(R.id.radioControlContainer, radioControl, "RadioControlFragment")
                .commit()
    }

    companion object {
        const val ID_JUMP_TO_AYAH = 0L
        const val ID_TAJWEED_RULES = ID_JUMP_TO_AYAH + 1
        const val ID_QURAN_MEMORIZATION = ID_TAJWEED_RULES + 1
        const val ID_AUDIO_MANAGER = ID_QURAN_MEMORIZATION + 1
        const val ID_SETTINGS = ID_AUDIO_MANAGER + 1
        const val ID_LEAVE_FEEDBACK = ID_SETTINGS + 1
        const val ID_RADIO = ID_LEAVE_FEEDBACK + 1
        const val ID_SHARE_APP = ID_RADIO + 1
        const val ID_RATE_APP = ID_SHARE_APP + 1
        const val ID_COPYRIGHT_INFO = ID_RATE_APP + 1
    }

}