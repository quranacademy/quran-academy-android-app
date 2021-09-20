package org.quranacademy.quran.bookmarks.ui.adapter

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hannesdorfmann.adapterdelegates3.AdapterDelegate
import org.quranacademy.quran.bookmarks.R
import org.quranacademy.quran.presentation.extensions.inflate

class CreateNewFolderAdapterDelegate(
        private val onCreateFolderClicked: () -> Unit
) : AdapterDelegate<MutableList<Any>>() {

    override fun isForViewType(items: MutableList<Any>, position: Int) =
            items[position] is NewFolderItem

    override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder =
            ViewHolder(parent.inflate(R.layout.item_bookmark_folder_create_new))

    override fun onBindViewHolder(items: MutableList<Any>,
                                  position: Int,
                                  viewHolder: RecyclerView.ViewHolder,
                                  payloads: MutableList<Any>) =
            (viewHolder as ViewHolder).bind()

    private inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        fun bind() {
            itemView.setOnClickListener {
                onCreateFolderClicked()
            }
        }
    }

}