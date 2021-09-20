package org.quranacademy.quran.mainscreen.presentation.ui.jumptoayah.input.autocomplete

import android.text.Spannable
import com.otaliastudios.autocomplete.AutocompletePolicy


class HQAAutocompletePolicy : AutocompletePolicy {

    override fun shouldShowPopup(text: Spannable, cursorPos: Int): Boolean {
        return true
    }

    override fun shouldDismissPopup(text: Spannable, cursorPos: Int): Boolean {
        return false
    }

    override fun getQuery(text: Spannable): CharSequence {
        return text
    }

    override fun onDismiss(text: Spannable) {

    }

}