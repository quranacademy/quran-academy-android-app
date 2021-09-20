package org.quranacademy.quran.mainscreen.presentation.ui.jumptoayah.input

import android.text.Editable
import org.quranacademy.quran.presentation.ui.global.TextWatcherAdapter

class NumberInputListener(private val listener: (number: Int) -> Unit) : TextWatcherAdapter() {

    override fun afterTextChanged(text: Editable) {
        val number = try {
            text.toString().toInt()
        } catch (error: NumberFormatException) {
            // this can happen if we are coming from IME_ACTION_GO
            return
        }
        listener(number)
    }

}