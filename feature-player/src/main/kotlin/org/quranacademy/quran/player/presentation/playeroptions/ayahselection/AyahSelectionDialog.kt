package org.quranacademy.quran.player.presentation.playeroptions.ayahselection

import android.os.Bundle
import android.text.InputFilter
import android.widget.EditText
import android.widget.NumberPicker
import androidx.fragment.app.DialogFragment
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import kotlinx.android.synthetic.main.dialog_ayah_selection.*
import org.quranacademy.quran.QuranConstants
import org.quranacademy.quran.di.bind
import org.quranacademy.quran.di.get
import org.quranacademy.quran.di.installModule
import org.quranacademy.quran.domain.models.AyahId
import org.quranacademy.quran.domain.models.Surah
import org.quranacademy.quran.player.R
import org.quranacademy.quran.presentation.mvp.routing.screens.AyahSelectionScreen
import org.quranacademy.quran.presentation.ui.base.BaseBottomSheetFragment
import toothpick.Scope
import java.lang.reflect.Field

class AyahSelectionDialog : BaseBottomSheetFragment(), AyahSelectionView {

    override val layoutRes: Int = R.layout.dialog_ayah_selection

    override val scopeModuleInstaller: (Scope) -> Unit = { scope ->
        val screen = screenResolver.getScreen<AyahSelectionScreen>(this)
        scope.installModule {
            bind(AyahSelectionScreen.Type::class)
                    .toInstance(screen.type)
            bind(AyahId::class)
                    .toInstance(screen.selectedAyah)
        }
    }

    @InjectPresenter
    lateinit var presenter: AyahSelectionPresenter

    @ProvidePresenter
    fun providePresenter() = scope.get<AyahSelectionPresenter>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setStyle(DialogFragment.STYLE_NORMAL, R.style.ThemeOverlay_AppTheme_BottomSheetDialog)
    }

    override fun setTitle(title: String) {
        titleLabel.text = title
    }

    override fun showInitialData(
            surahs: List<Surah>,
            ayahsCount: Int,
            currentSurah: Surah,
            currentAyahNumber: Int
    ) {
        surahValue.apply {
            minValue = 1
            maxValue = QuranConstants.SURAHS_COUNT
            value = currentSurah.surahNumber

            textFormatterBugFix()
            setFormatter { value ->
                if (value == -1) return@setFormatter "-"
                val index = if (value > 0) value - 1 else value
                "${value}. ${surahs[index].transliteratedName}"
            }
            setOnValueChangedListener { _, _, newVal ->
                presenter.onSurahSelected(newVal)
            }
        }
        ayahValue.apply {
            minValue = 1
            maxValue = surahs[currentSurah.surahNumber - 1].ayahsCount
            value = currentAyahNumber

            setOnValueChangedListener { _, _, newVal ->
                presenter.onAyahSelected(newVal)
            }
        }

        selectButton.setOnClickListener {
            presenter.onOkButtonClicked()
        }
    }

    override fun updateAyahValues(ayahsCount: Int) {
        ayahValue.apply {
            minValue = 1
            maxValue = ayahsCount
            if (value > ayahsCount) {
                value = 1
            }
            setOnValueChangedListener { _, _, newVal ->
                presenter.onAyahSelected(newVal)
            }
        }
    }

    /*
     * NumberPicker with fixed bug, when initial value not formatted
     * https://stackoverflow.com/questions/17708325/android-numberpicker-with-formatter-doesnt-format-on-first-rendering
     */
    private fun NumberPicker.textFormatterBugFix() {
        val f: Field = NumberPicker::class.java.getDeclaredField("mInputText")
        f.isAccessible = true
        val inputText: EditText = f.get(this) as EditText
        inputText.filters = arrayOfNulls<InputFilter>(0)
    }

}