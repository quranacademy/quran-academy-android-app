package org.quranacademy.quran.player.presentation.playeroptions

import android.os.Bundle
import android.view.View
import kotlinx.android.synthetic.main.dialog_repetitions_count.*
import org.quranacademy.quran.player.R
import org.quranacademy.quran.player.presentation.global.extensions.argument
import org.quranacademy.quran.presentation.ui.base.BaseBottomSheetFragment
import org.quranacademy.quran.presentation.ui.global.getCurrentAppLanguage
import org.quranacademy.quran.presentation.ui.global.isArabic
import org.quranacademy.quran.presentation.ui.global.toArabicNumberIfNeeded

class RepetitionsCountDialog : BaseBottomSheetFragment() {

    override val layoutRes: Int = R.layout.dialog_repetitions_count

    private var repeatsCount: Int by argument(REPEATS_COUNT_ARG)
    private lateinit var onValueSelectedListener: ((repeatsCount: Int) -> Unit)
    private val isCurrentLanguageArabic = getCurrentAppLanguage().isArabic()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        onRepetitionsCountValueUpdated()
        singleRepeatButton.setOnClickListener {
            repeatsCount = 1
            onRepetitionsCountValueUpdated()
        }
        endlessRepeatButton.setOnClickListener {
            repeatsCount = ENDLESS_REPEAT
            onRepetitionsCountValueUpdated()
        }
        decreaseRepeatsCount.setOnClickListener {
            changeRepeatsCount(false)
        }
        increaseRepeatsCount.setOnClickListener {
            changeRepeatsCount(true)
        }
        selectButton.setOnClickListener {
            onValueSelectedListener(repeatsCount)
            dismiss()
        }
    }

    fun onValueSelected(listener: (repeatsCount: Int) -> Unit): RepetitionsCountDialog {
        this.onValueSelectedListener = listener
        return this
    }

    private fun changeRepeatsCount(increase: Boolean) {
        if (repeatsCount == ENDLESS_REPEAT) {
            repeatsCount = 1
            repetitionsCountValue.text = repeatsCount.toString()
            return
        }

        if (increase && repeatsCount + 1 <= MAX_REPEATS_COUNT) {
            repeatsCount++
        } else if (!increase && repeatsCount > 1) {
            repeatsCount--
        }

        onRepetitionsCountValueUpdated()
    }

    private fun onRepetitionsCountValueUpdated() {
        repetitionsCountValue.text = getRepetitionsCountRepresentation(repeatsCount, isCurrentLanguageArabic)
    }

    companion object {

        const val TAG = "RepetitionsCountDialog"

        const val ENDLESS_REPEAT = -1
        const val MIN_REPEATS_COUNT = 1
        const val MAX_REPEATS_COUNT = 20

        private const val REPEATS_COUNT_ARG = "repeats_count"

        fun newInstance(repeatsCount: Int): RepetitionsCountDialog {
            val fragment = RepetitionsCountDialog()
            fragment.arguments = Bundle().apply {
                putInt(REPEATS_COUNT_ARG, repeatsCount)
            }
            return fragment
        }

        fun getRepetitionsCountRepresentation(count: Int, isCurrentLanguageArabic: Boolean): String {
            return if (count == ENDLESS_REPEAT) {
                "∞"
            } else {
                count.toArabicNumberIfNeeded(isCurrentLanguageArabic)
            }
        }

    }

}