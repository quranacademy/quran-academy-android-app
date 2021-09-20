package org.quranacademy.quran.memorization.ui.options

import android.content.pm.PackageManager
import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import com.afollestad.assent.Permission
import com.afollestad.assent.askForPermissions
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.list.listItems
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import kotlinx.android.synthetic.main.activity_memorization_options.*
import org.quranacademy.quran.QuranConstants
import org.quranacademy.quran.core.permissions.createHQAPermissionsRationale
import org.quranacademy.quran.core.permissions.isPermissionBlockedFromAsking
import org.quranacademy.quran.core.permissions.showGrantPermissionFromSettingsDialog
import org.quranacademy.quran.di.bindPrimitive
import org.quranacademy.quran.di.get
import org.quranacademy.quran.di.installModule
import org.quranacademy.quran.domain.models.Recitation
import org.quranacademy.quran.domain.models.Surah
import org.quranacademy.quran.memorization.R
import org.quranacademy.quran.memorization.models.MemorizationMode
import org.quranacademy.quran.memorization.mvp.options.MemorizationOptionsPresenter
import org.quranacademy.quran.memorization.mvp.options.MemorizationOptionsView
import org.quranacademy.quran.memorization.routing.MemorizationOptionsScreen
import org.quranacademy.quran.presentation.extensions.visible
import org.quranacademy.quran.presentation.ui.base.BaseThemedActivity
import org.quranacademy.quran.presentation.ui.global.toArabicNumberIfNeeded
import toothpick.Scope

class MemorizationOptionsActivity : BaseThemedActivity(), MemorizationOptionsView {

    override val layoutRes = R.layout.activity_memorization_options

    private val toolbar: Toolbar by lazy { findViewById<Toolbar>(R.id.toolbar) }

    override val scopeModuleInstaller: (Scope) -> Unit = {
        val screen = screenResolver.getScreen<MemorizationOptionsScreen>(this)
        it.installModule {
            bindPrimitive("options", screen.memorizationOptions)
        }
    }

    @InjectPresenter
    lateinit var presenter: MemorizationOptionsPresenter

    @ProvidePresenter
    fun providePresenter() = scope.get<MemorizationOptionsPresenter>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp)
        toolbar.setNavigationOnClickListener { onBackPressed() }
        toolbar.setTitle(R.string.quran_memorization)

        memorizationModeValue.setOnClickListener {
            presenter.onSelectMemorizationModeClicked()
        }

        pageNumberValue.setOnClickListener {
            presenter.onSelectQuranPageClicked()
        }

        surahValue.setOnClickListener {
            presenter.onSelectSurahClicked()
        }

        ayahsRangeValue.setOnClickListener {
            presenter.onSelectAyahsRangeClicked()
        }

        ayahRepetitionsCountValue.setOnClickListener {
            presenter.onSelectRepetitionsCountClicked()
        }

        delayBetweenRepetitionsValue.setOnClickListener {
            presenter.onSelectDelayBetweenRepetitionsClicked()
        }

        recitationValue.setOnClickListener {
            presenter.onSelectRecitationButtonClicked()
        }

        startMemorizationButton.setOnClickListener {
            val rationaleHandler = createHQAPermissionsRationale {
                onPermission(Permission.READ_EXTERNAL_STORAGE, R.string.external_permission_not_needed_message)
            }

            askForPermissions(Permission.READ_EXTERNAL_STORAGE, rationaleHandler = rationaleHandler) { result ->
                val grantResult = result.grantResults.first()
                if (grantResult == PackageManager.PERMISSION_GRANTED) {
                    presenter.onStartMemorizationClicked()
                } else {
                    if (isPermissionBlockedFromAsking(Permission.READ_EXTERNAL_STORAGE)) {
                        showGrantPermissionFromSettingsDialog(this)
                    } else {
                        presenter.onStartMemorizationClicked()
                    }
                }
            }
        }

        openMemorizationHelpButton.setOnClickListener {
            presenter.onShowMemorizationHelpClicked()
        }
    }

    override fun showInitProgressBar(isVisible: Boolean) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun showMemorizationModesSelect(modes: List<MemorizationMode>) {
        MaterialDialog(this).show {
            title(R.string.choose_memorizing_mode)
            val memorizationModes = modes.map { it.getTitle() }
            listItems(items = memorizationModes) { _, index, _ ->
                presenter.onMemorizationModeSelected(modes[index])
            }
        }
    }

    override fun updateMemorizationMode(mode: MemorizationMode) {
        memorizationModeValue.text = mode.getTitle()

        val isByPageMode = mode == MemorizationMode.PAGE
        pageMemorizationModeGroup.visible(isByPageMode)
        ayahsRangeMemorizationModeGroup.visible(!isByPageMode)
    }


    override fun updateSurahValue(surahNumber: Int, name: String) {
        surahValue.text = "${surahNumber}. $name"
    }

    override fun updateAyahsRangeValue(range: Pair<Int, Int>) {
        ayahsRangeValue.text = "${range.first} - ${range.second}"
    }

    override fun showQuranPageSelection() {
        MaterialDialog(this).show {
            title(R.string.select_page_number_title)
            val pageNumberValues = (1..QuranConstants.QURAN_PAGES_COUNT).map { it.toString() }
            listItems(items = pageNumberValues) { _, index, _ ->
                presenter.onQuranPageSelected(index + 1)
            }
        }
    }

    override fun updatePageValue(pageNumber: Int) {
        pageNumberValue.text = pageNumber.toString()
    }

    override fun showSurahSelection(surahs: List<Surah>) {
        MaterialDialog(this).show {
            title(R.string.select_surah)
            val surahTitles = surahs.map { "${it.surahNumber}. ${it.transliteratedName}" }
            listItems(items = surahTitles) { _, index, _ ->
                presenter.onSurahSelected(surahs[index])
            }
        }
    }

    override fun showAyahsRangeSelection(currentAyahsRange: Pair<Int, Int>, maxAyahValue: Int) {
        AyahsRangeSelectionDialog(this, currentAyahsRange, maxAyahValue) {
            presenter.onAyahsRangeSelected(it)
        }
    }

    override fun showRepetitionsSelectionDialog(repetitionCounts: List<Int>) {
        MaterialDialog(this).show {
            title(R.string.select_repetitions_count)
            val repetitionCountTitles = repetitionCounts.map { it.toString() }
            listItems(items = repetitionCountTitles) { _, index, _ ->
                presenter.onRepetitionsCountSelected(repetitionCounts[index])
            }
        }
    }

    override fun updateRepetitionsValue(count: Int) {
        val timesLabelFormatted = resources.getQuantityString(R.plurals.repetition_count_option_value, count)
        ayahRepetitionsCountValue.text = String.format(timesLabelFormatted, count.toArabicNumberIfNeeded())
    }

    override fun showDelayBetweenRepetitionsDialog(delayValues: List<Int>) {
        MaterialDialog(this).show {
            title(R.string.select_delay_between_repetitions)
            val repetitionCountTitles = delayValues.map { it.formatMilliseconds() }
            listItems(items = repetitionCountTitles) { _, index, _ ->
                presenter.onDelayBetweenRepetitionsSelected(delayValues[index])
            }
        }
    }

    override fun updateDelayBetweenRepetitionsValue(value: Int) {
        delayBetweenRepetitionsValue.text = value.formatMilliseconds()
    }

    override fun showRecitationSelectDialog(recitations: List<Recitation>) {
        MaterialDialog(this).show {
            title(R.string.select_recitation)
            val recitationTitles = recitations.map { it.name }
            listItems(items = recitationTitles) { _, index, _ ->
                presenter.onRecitationSelected(recitations[index])
            }
        }
    }

    override fun updateRecitationValue(name: String) {
        recitationValue.text = name
    }

    private fun MemorizationMode.getTitle(): String {
        val modeTitles = mapOf(
                MemorizationMode.PAGE to R.string.page_memorization_mode,
                MemorizationMode.SURAH to R.string.surah_memorization_mode
        )
        return getString(modeTitles[this]!!)
    }

    private fun Int.formatMilliseconds() = "%.1f".format(0.001f * this)

}