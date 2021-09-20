package org.quranacademy.quran.mainscreen.presentation.ui.jumptoayah.input.autocomplete

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Handler
import android.text.Editable
import android.widget.EditText
import com.otaliastudios.autocomplete.Autocomplete
import com.otaliastudios.autocomplete.AutocompleteCallback

class InputAutoComplete<T>(
        private val input: EditText,
        private val autocomplete: Autocomplete<T>,
        private val presenter: InputAutocompletePresenter<T>,
        private val onSelectedListener: OnSelectedListener<T>
) {

    fun setItems(data: List<T>, onItemSelected: (T) -> Unit) {
        input.setText(data[0].toString())
        this.presenter.setData(data)
        this.onSelectedListener.setListener(onItemSelected)
        //зыкрываем всплывающий диалог с подсказками
        Handler().postDelayed({ autocomplete.dismissPopup() }, 50L)
    }

    fun getItems() = presenter.getData()

    companion object {

        fun <T> addAutoComplete(input: EditText): InputAutoComplete<T> {
            val onSelectedListener = OnSelectedListener<T>()
            var autocomplete: Autocomplete<T>? = null
            val presenter = InputAutocompletePresenter<T>(input) {
                onSelectedListener.onSelected(it)
                input.setText(it.toString())
                autocomplete?.dismissPopup()
            }
            val callback = object : AutocompleteCallback<T> {
                override fun onPopupItemClicked(editable: Editable, item: T): Boolean {
                    editable.clear()
                    editable.append(item.toString())
                    return true
                }

                override fun onPopupVisibilityChanged(shown: Boolean) {}
            }

            autocomplete = Autocomplete.on<T>(input)
                    .with(HQAAutocompletePolicy())
                    .with(ColorDrawable(Color.WHITE))
                    .with(presenter)
                    .with(callback)
                    .build()
            return InputAutoComplete(input, autocomplete, presenter, onSelectedListener)
        }

    }

    class OnSelectedListener<T> {

        private var listener: ((T) -> Unit)? = null

        fun setListener(listener: (T) -> Unit) {
            this.listener = listener
        }

        fun onSelected(value: T) {
            listener?.invoke(value)
        }

    }

}