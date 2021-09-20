package org.quranacademy.quran.mainscreen.presentation.ui.jumptoayah.input.autocomplete

import android.text.TextUtils
import android.view.ViewGroup
import android.widget.EditText
import androidx.recyclerview.widget.RecyclerView
import com.otaliastudios.autocomplete.RecyclerViewPresenter

class InputAutocompletePresenter<T>(
        private val input: EditText,
        onItemSelected: (T) -> Unit
) : RecyclerViewPresenter<T>(input.context) {

    private var data: List<T>? = null
    private var adapter = InputAutoCompleteAdapter(onItemSelected)

    fun getData() = data

    fun setData(data: List<T>?) {
        this.data = data
    }

    override fun getPopupDimensions(): PopupDimensions {
        val dims = PopupDimensions()
        dims.width = input.width
        dims.height = ViewGroup.LayoutParams.WRAP_CONTENT
        return dims
    }

    override fun instantiateAdapter(): RecyclerView.Adapter<*> = adapter

    override fun onQuery(query: CharSequence?) {
        if (TextUtils.isEmpty(query)) {
            adapter.setData(data)
        } else {
            val query = query!!.toString().toLowerCase()
            val result = data?.let { items ->
                items.filter {
                    val item = it.toString()
                    item.contains(query) || item.contains(query)
                }
            } ?: emptyList()
            adapter.setData(result)
        }
        adapter.notifyDataSetChanged()
    }

}