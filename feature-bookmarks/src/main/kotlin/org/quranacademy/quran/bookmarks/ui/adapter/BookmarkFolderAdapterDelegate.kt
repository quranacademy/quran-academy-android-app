package org.quranacademy.quran.bookmarks.ui.adapter

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hannesdorfmann.adapterdelegates3.AdapterDelegate
import kotlinx.android.synthetic.main.item_dialog_bookmark_folder.view.*
import org.quranacademy.quran.bookmarks.R
import org.quranacademy.quran.bookmarks.domain.models.BookmarkFolder
import org.quranacademy.quran.presentation.extensions.inflate

class BookmarkFolderAdapterDelegate(
        private val onFolderSelected: (BookmarkFolder) -> Unit
) : AdapterDelegate<MutableList<Any>>() {

    private var folderAdapterDelegate: Long? = null

    override fun isForViewType(items: MutableList<Any>, position: Int) =
            items[position] is BookmarkFolder

    override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder =
            ViewHolder(parent.inflate(R.layout.item_dialog_bookmark_folder))

    override fun onBindViewHolder(
            items: MutableList<Any>,
            position: Int,
            viewHolder: RecyclerView.ViewHolder,
            payloads: MutableList<Any>
    ) = (viewHolder as ViewHolder).bind(items[position] as BookmarkFolder)

    fun setBookmarkFolderId(folderAdapterDelegate: Long) {
        this.folderAdapterDelegate = folderAdapterDelegate
    }

    private inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        fun bind(folder: BookmarkFolder) {

            val isCurrentFolder = folder.id == folderAdapterDelegate
            itemView.folderSelectedIndicator.isChecked = isCurrentFolder
            itemView.folderName.text = folder.name
            itemView.setOnClickListener {
                onFolderSelected(folder)
            }
        }

    }

}