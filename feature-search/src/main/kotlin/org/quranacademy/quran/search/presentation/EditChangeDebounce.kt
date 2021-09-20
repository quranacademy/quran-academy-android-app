package org.quranacademy.quran.search.presentation

import android.text.Editable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.quranacademy.quran.presentation.ui.global.TextWatcherAdapter

open class EditChangeDebounce(
        private val scope: CoroutineScope,
        private val action: (text: String) -> Unit
) : TextWatcherAdapter() {

    private var searchFor = ""

    override fun afterTextChanged(text: Editable) {
        val searchText = text.toString()
        if (searchText == searchFor)
            return

        searchFor = searchText

        scope.launch {
            delay(500)  //debounce timeOut
            if (searchText != searchFor)
                return@launch
            action(text.toString())
        }
    }
}