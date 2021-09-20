package org.quranacademy.quran.ui.preferences.io

import android.content.Context

interface UserInputModule {

    fun showEditTextInput(
            key: String,
            title: String,
            defaultValue: String,
            listener: Listener<String>
    )

    fun showSingleChoiceInput(
            key: String,
            title: CharSequence,
            displayItems: Array<String>,
            values: Array<String>,
            selected: Int,
            listener: Listener<String>
    )

    fun showMultiChoiceInput(
            key: String,
            title: CharSequence,
            displayItems: Array<String>,
            values: Array<String>,
            defaultSelection: BooleanArray,
            listener: Listener<Set<String>>
    )

    interface Factory {
        fun create(context: Context): UserInputModule
    }

    interface Listener<T> {
        fun onInput(value: T)
    }
}
