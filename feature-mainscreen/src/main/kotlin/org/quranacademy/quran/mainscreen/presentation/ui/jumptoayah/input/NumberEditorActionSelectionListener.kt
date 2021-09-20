package org.quranacademy.quran.mainscreen.presentation.ui.jumptoayah.input

import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import android.widget.TextView

class NumberEditorActionSelectionListener(
        private val listener: (number: Int) -> Unit
) : TextView.OnEditorActionListener {

    override fun onEditorAction(v: TextView, actionId: Int, event: KeyEvent?): Boolean {
        if (actionId == EditorInfo.IME_ACTION_GO) {
            val number = try {
                v.text.toString().toInt()
            } catch (error: NumberFormatException) {
                // this can happen if we are coming from IME_ACTION_GO
                return false
            }
            listener(number)
        }
        return true
    }

}