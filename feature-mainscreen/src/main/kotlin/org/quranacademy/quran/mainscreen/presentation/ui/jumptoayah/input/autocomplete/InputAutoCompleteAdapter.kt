package org.quranacademy.quran.mainscreen.presentation.ui.jumptoayah.input.autocomplete

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.edittext_autocomple_item.view.*
import org.quranacademy.quran.mainscreen.R
import org.quranacademy.quran.presentation.extensions.inflate

class InputAutoCompleteAdapter<T>(
        private val onItemSelected: (T) -> Unit
) : RecyclerView.Adapter<InputAutoCompleteAdapter<T>.Holder>() {

    private var data: List<T>? = null

    inner class Holder internal constructor(
            root: View
    ) : RecyclerView.ViewHolder(root)

    fun setData(data: List<T>?) {
        this.data = data
    }

    override fun getItemCount(): Int = data?.size ?: 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        return Holder(parent.inflate(R.layout.edittext_autocomple_item, false))
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val item = data!![position]
        holder.itemView.text.text = item.toString()
        holder.itemView.setOnClickListener { onItemSelected(item) }
    }
}