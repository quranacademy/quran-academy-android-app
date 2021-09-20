package org.quranacademy.quran.mainscreen.presentation.ui.jumptoayah

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.app.AppCompatDialog
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.list.listItems
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import kotlinx.android.synthetic.main.dialog_jump_to_ayah.view.*
import org.quranacademy.quran.di.get
import org.quranacademy.quran.domain.models.Surah
import org.quranacademy.quran.mainscreen.R
import org.quranacademy.quran.mainscreen.presentation.mvp.jumptoayah.JumpToAyahPresenter
import org.quranacademy.quran.mainscreen.presentation.mvp.jumptoayah.JumpToAyahView
import org.quranacademy.quran.mainscreen.presentation.ui.jumptoayah.input.NumberEditorActionSelectionListener
import org.quranacademy.quran.mainscreen.presentation.ui.jumptoayah.input.NumberInputListener
import org.quranacademy.quran.mainscreen.presentation.ui.jumptoayah.input.autocomplete.InputAutoComplete
import org.quranacademy.quran.presentation.extensions.inflate
import org.quranacademy.quran.presentation.extensions.setSize
import org.quranacademy.quran.presentation.ui.base.BaseDialogFragment

class JumpToAyahDialog : BaseDialogFragment(), JumpToAyahView {

    private lateinit var dialogContentView: View

    @InjectPresenter
    lateinit var presenter: JumpToAyahPresenter

    @ProvidePresenter
    fun providePresenter() = scope.get<JumpToAyahPresenter>()

    private val surahAutoComplete by lazy {
        InputAutoComplete.addAutoComplete<String>(dialogContentView.surahValue)
    }

    private val ayahAutoComplete by lazy {
        InputAutoComplete.addAutoComplete<Int>(dialogContentView.ayahValue)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        dialogContentView = inflate(R.layout.dialog_jump_to_ayah, null)
        with(dialogContentView) {
            pageNumberField.addTextChangedListener(NumberInputListener { pageNumber ->
                presenter.onPageInput(pageNumber)
            })
            pageNumberField.setOnEditorActionListener(NumberEditorActionSelectionListener { pageNumber ->
                presenter.onPageSelected(pageNumber)
            })
            jumpToAyahButton.setOnClickListener { presenter.onOkButtonClicked() }
        }

        return AppCompatDialog(context).apply {
            requestWindowFeature(Window.FEATURE_NO_TITLE)
            window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            setContentView(dialogContentView)
            setSize(0.95f)
            //нужно, чтобы избежать перекрытия dropdown-списка клавиатурой
            window!!.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        }
    }

    override fun showSurahsList(surahs: List<Surah>) {
        dialogContentView.pageNumberField.hint = 1.toString() //first page

        val surahTitles = surahs.map { "${it.surahNumber}. ${it.transliteratedName}" }
        surahAutoComplete.setItems(surahTitles) {
            val surahNumber = surahTitles.indexOf(it) + 1
            presenter.onSurahSelected(surahNumber)
        }
    }

    override fun showPageNumberHint(pageNumber: Int) {
        dialogContentView.pageNumberField.setText("")
        dialogContentView.pageNumberField.hint = pageNumber.toString()
    }

    override fun setCurrentSurahAndAyah(surahNumber: Int, selectedAyah: Int) {
        with(dialogContentView) {
            ayahValue.setText(selectedAyah.toString())

            val surahItem = surahAutoComplete.getItems()!![surahNumber - 1]
            surahValue.setText(surahItem)
        }
    }

    override fun configureAyahSelector(ayahsCount: Int, currentAyah: Int) {
        val ayahNumbers = (1..ayahsCount).map { it }
        ayahAutoComplete.setItems(ayahNumbers) {
            presenter.onAyahSelected(it)
        }
    }

    override fun showReadModeSelectionDialog() {
        val readModes = listOf(getString(R.string.list_reading_mode), getString(R.string.mushaf_reading_mode))
        MaterialDialog(context!!).show {
            title(R.string.select_reading_mode_title)
            listItems(items = readModes) { _, index, _ ->
                when (index) {
                    0 -> presenter.onReadModeSelected(false)
                    1 -> presenter.onReadModeSelected(true)
                }
            }
        }
    }

}