package org.quranacademy.quran.surahdetails.ui

import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.widget.Toolbar
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import kotlinx.android.synthetic.main.activity_surah_details.*
import me.aartikov.alligator.NavigationContext
import me.aartikov.alligator.Screen
import me.aartikov.alligator.ScreenResult
import org.quranacademy.quran.di.bindSingleton
import org.quranacademy.quran.di.get
import org.quranacademy.quran.di.getGlobal
import org.quranacademy.quran.di.installModule
import org.quranacademy.quran.domain.models.AyahId
import org.quranacademy.quran.player.presentation.global.QuranPlayer
import org.quranacademy.quran.player.presentation.playeroptions.PlayerOptionsShell
import org.quranacademy.quran.presentation.extensions.dp
import org.quranacademy.quran.presentation.extensions.visible
import org.quranacademy.quran.presentation.textsettingspanel.TextSettingsPanel
import org.quranacademy.quran.presentation.ui.base.BaseThemedActivity
import org.quranacademy.quran.presentation.ui.global.DisabledAnimationProvider
import org.quranacademy.quran.presentation.ui.global.OnScrollListener
import org.quranacademy.quran.presentation.ui.global.toArabicNumberIfNeeded
import org.quranacademy.quran.surahdetails.R
import org.quranacademy.quran.surahdetails.mvp.SurahDetailsPresenter
import org.quranacademy.quran.surahdetails.mvp.SurahDetailsUiModel
import org.quranacademy.quran.surahdetails.mvp.SurahDetailsView
import org.quranacademy.quran.surahdetails.ui.scroll.AyahListScrollManager
import toothpick.Scope
import kotlin.math.absoluteValue

class SurahDetailsActivity : BaseThemedActivity(), SurahDetailsView {

    override val layoutRes = R.layout.activity_surah_details

    override val scopeModuleInstaller: (Scope) -> Unit = {
        it.installModule {
            bindSingleton<PlayerOptionsShell>()
        }
    }

    @InjectPresenter
    lateinit var presenter: SurahDetailsPresenter

    private val surahAyahsAdapter by lazy {
        SurahAyahsAdapter(
                context = this,
                coroutineScope = this,
                appearanceManager = appearanceManager,
                appPreferences = getGlobal(),
                onAyahClickListener = { presenter.onAyahSelected(it) },
                onNextSurahClickListener = { presenter.onOpenNextSurahClicked() }
        )
    }
    private val ayahListScrollManager by lazy {
        AyahListScrollManager(ayahsList)
    }

    private val textSettingsPanel = TextSettingsPanel()
    private val toolbar: Toolbar by lazy { findViewById<Toolbar>(R.id.toolbar) }
    private val player by lazy { QuranPlayer(supportFragmentManager) }

    @ProvidePresenter
    fun providePresenter() = scope.get<SurahDetailsPresenter>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        toolbar.setSubtitleTextAppearance(this, R.style.Toolbar_SubtitleText)
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp)
        toolbar.setNavigationOnClickListener { presenter.onBackPressed() }
        toolbar.inflateMenu(R.menu.surah_details_menu)
        toolbar.setOnMenuItemClickListener { onMenuItemClick(it.itemId) }
        toolbar.menu.localizeMenu()

        ayahsList.setHasFixedSize(true)
        ayahsList.adapter = surahAyahsAdapter
        ayahsList.addOnScrollListener(OnScrollListener(OnScrollListener.State.ENDED) {
            presenter.saveLastReadPosition(getCurrentAyahPosition())
        })

        openPlayerFab.setOnClickListener { presenter.onOpenPlayerButtonClicked() }

        var lastScrollUpdate = System.currentTimeMillis()
        ayahListScrollManager.onListScrollListener { _, dy ->
            val currentTime = System.currentTimeMillis()
            if (currentTime - lastScrollUpdate > 300) {
                presenter.onScrolled(getCurrentAyahPosition())
                lastScrollUpdate = currentTime
            }
        }

        ayahListScrollManager.onListScrollListener { _, dy ->
            val length = 5.dp
            if (dy > length) {
                if (openPlayerFab.isShown) openPlayerFab.hide()
            } else if (dy < -length) {
                if (!openPlayerFab.isShown) openPlayerFab.show()
            }
        }

        textSettingsPanel.onDismissListener = {
            presenter.onTextSettingsPanelClosed()
        }

    }

    override fun showProgressLayout(isVisible: Boolean) {
        progressLayout.visible(isVisible)
    }

    override fun openTextSettingsPanelClicked(isOpened: Boolean) {
        if (isOpened) {
            textSettingsPanel.show(supportFragmentManager, "TextSettingsBottomSheet")
        } else {
            textSettingsPanel.dismiss()
        }
    }

    override fun showSurah(surah: SurahDetailsUiModel, ayahNumber: Int) {
        toolbar.title = surah.transliteratedName
        surahAyahsAdapter.setData(surah)

        //if ayahNumber is first ayah, stay at the top og the list to show surah header
        if (ayahNumber > 1) {
            ayahsList.post { ayahListScrollManager.scrollToAyah(ayahNumber) }
        }
    }

    override fun updateCurrentPositionInfo(pageNumber: Int, juzNumber: Int) {
        toolbar.subtitle = getString(
                R.string.current_reading_position_info,
                pageNumber.toArabicNumberIfNeeded(),
                juzNumber.toArabicNumberIfNeeded()
        )
    }

    override fun showPlayerButton(isShowing: Boolean) {
        if (isShowing) {
            openPlayerFab.show()
        } else {
            openPlayerFab.hide()
        }
    }

    override fun showPlayerOptionsPanel(startAyah: AyahId, endAyah: AyahId) {
        player.showPlayerOptionsPanel(startAyah, endAyah)
    }

    override fun showPlayerControlPanel() = player.showPlayerControlPanel()

    override fun hidePlayerControlPanel() = player.hidePlayerControlPanel()

    override fun onBackPressed() = presenter.onBackPressed()

    override fun createNavigationContext(): NavigationContext.Builder {
        return super.createNavigationContext()
                .transitionAnimationProvider(DisabledAnimationProvider())
    }

    override fun onScreenResult(screenClass: Class<out Screen>, result: ScreenResult?) {
        player.onScreenResult(screenClass, result)
    }

    override fun highlightNowPlayingAyah(ayahNumber: Int?) {
        surahAyahsAdapter.highlightNowPlayingAyah(ayahNumber)
    }

    override fun scrollToAyah(ayahNumber: Int) {
        ayahsList.post {
            val ayahsRange = (getCurrentAyahPosition() - ayahNumber).absoluteValue
            //если аятов больше десяти, то применяем быстрый (мгноаенный) скролл без медленной прокрутки
            if (ayahsRange > 10) {
                ayahListScrollManager.scrollToAyah(ayahNumber)
            } else {
                ayahListScrollManager.smoothScrollToAyah(ayahNumber)
            }
        }
    }

    override fun highlightNowPlayingWord(wordNumber: Int?) {
        surahAyahsAdapter.highlightNowPlayingWord(wordNumber)
    }

    private fun onMenuItemClick(itemId: Int): Boolean {
        when (itemId) {
            R.id.action_open_settings_screen -> {
                presenter.onOpenSettingsScreenClicked()
            }
            R.id.action_open_text_size_settings_panel -> {
                presenter.onOpenTranslationTextSettingsPanelClicked()
            }
            R.id.action_switch_reader_mode -> {
                presenter.onSwitchReaderModeClicked(getCurrentAyahPosition())
            }
        }
        return true
    }

    /**
     * т. к. первым пунктом в списке является заголовок, то нумерация аятов
     * будет начинаться с 1 и совпадать с позицией в списке
     */
    private fun getCurrentAyahPosition(): Int {
        val firstVisibleItemPosition = ayahListScrollManager.getFirstVisibleAyah()
        val isListEmpty = firstVisibleItemPosition == -1
        //пользователь находится в начале списка, первый пункт = заголовок суры
        val isFirstItem = firstVisibleItemPosition == 0
        if (isListEmpty || isFirstItem) {
            return 1
        }
        return firstVisibleItemPosition
    }

}