package org.quranacademy.quran.memorization.ui.options

import android.content.Context
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.list.listItems
import kotlinx.android.synthetic.main.dialog_ayahs_range_selection.view.*
import org.quranacademy.quran.memorization.R
import org.quranacademy.quran.presentation.extensions.inflate

class AyahsRangeSelectionDialog(
        private val context: Context,
        initialValue: Pair<Int, Int>,
        maxAyahValue: Int,
        onResult: (ayahsRange: Pair<Int, Int>) -> Unit
) {

    private val view by lazy { context.inflate(R.layout.dialog_ayahs_range_selection) }
    private var startAyah = 0
        set(value) {
            field = value
            view.startAyahValue.text = value.toString()
        }
    private var endAyah = 0
        set(value) {
            field = value
            view.endAyahValue.text = value.toString()
        }

    init {
        startAyah = initialValue.first
        endAyah = initialValue.second

        view.startAyahValue.setOnClickListener {
            showSelectionDialog(maxAyahValue) {
                startAyah = it

                if (startAyah > endAyah) {
                    endAyah = startAyah
                }
            }
        }
        view.endAyahValue.setOnClickListener {
            showSelectionDialog(maxAyahValue) {
                endAyah = it

                if (startAyah > endAyah) {
                    startAyah = endAyah
                }
            }
        }

        val dialog = MaterialDialog(context).show {
            title(R.string.select_ayahs_range_title)
            customView(view = view)
        }
        dialog.show()
        view.setAyahsRangeButton.setOnClickListener {
            onResult(Pair(startAyah, endAyah))
            dialog.dismiss()
        }
    }

    private fun showSelectionDialog(ayahsCount: Int, onAyahSelected: (selectedAyahNumber: Int) -> Unit) {
        MaterialDialog(context).show {
            val surahTitles = (1..ayahsCount).map { it.toString() }
            listItems(items = surahTitles) { _, index, _ ->
                onAyahSelected(index + 1)
            }
        }
    }

}