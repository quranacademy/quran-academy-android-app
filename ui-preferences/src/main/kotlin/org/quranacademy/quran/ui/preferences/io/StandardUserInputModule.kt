package org.quranacademy.quran.ui.preferences.io

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.list.listItemsMultiChoice
import com.afollestad.materialdialogs.list.listItemsSingleChoice
import org.quranacademy.quran.ui.preferences.R

class StandardUserInputModule(
        protected var context: Context
) : UserInputModule {

    override fun showEditTextInput(
            key: String,
            title: String,
            defaultValue: String,
            listener: UserInputModule.Listener<String>
    ) {
        val view = LayoutInflater.from(context).inflate(R.layout.dialog_edittext, null)
        val inputField = view.findViewById(R.id.mp_text_input) as EditText

        inputField.setText(defaultValue)
        inputField.setSelection(defaultValue.length)

        val dialog = AlertDialog.Builder(context)
                .setTitle(title)
                .setView(view)
                .show()

        view.findViewById<View>(R.id.mp_btn_confirm).setOnClickListener {
            listener.onInput(inputField.text.toString())
            dialog.dismiss()
        }
    }

    override fun showSingleChoiceInput(
            key: String,
            title: CharSequence,
            displayItems: Array<String>,
            values: Array<String>,
            selected: Int,
            listener: UserInputModule.Listener<String>
    ) {
        MaterialDialog(context).show {
            title(text = title.toString())
            listItemsSingleChoice(
                    items = displayItems.asList(),
                    initialSelection = selected
            ) { _, index, _ ->
                listener.onInput(values[index])
            }
        }
    }

    override fun showMultiChoiceInput(
            key: String,
            title: CharSequence,
            displayItems: Array<String>,
            values: Array<String>,
            defaultSelection: BooleanArray,
            listener: UserInputModule.Listener<Set<String>>
    ) {

        MaterialDialog(context).show {
            title(text = title.toString())
            val selectedIndices = defaultSelection
                    .mapIndexed { index, isChecked ->
                        if (isChecked) index else -1
                    }
                    .filter { it != -1 }
                    .toIntArray()
            listItemsMultiChoice(
                    items = displayItems.asList(), initialSelection = selectedIndices
            ) { _, indices, _ ->
                val result = indices.map { values[it] }
                listener.onInput(result.toSet())
            }
        }
    }

}
