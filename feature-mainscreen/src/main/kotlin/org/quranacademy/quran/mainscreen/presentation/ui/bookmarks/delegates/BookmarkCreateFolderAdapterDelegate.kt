package org.quranacademy.quran.mainscreen.presentation.ui.bookmarks.delegates

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hannesdorfmann.adapterdelegates3.AdapterDelegate
import kotlinx.android.synthetic.main.item_create_bookmark_folder.view.*
import org.quranacademy.quran.mainscreen.R
import org.quranacademy.quran.mainscreen.presentation.mvp.bookmarkslist.CreateFolderModel
import org.quranacademy.quran.presentation.extensions.inflate

class BookmarkCreateFolderAdapterDelegate(
        private val onCreateFolderClicked: () -> Unit
) : AdapterDelegate<MutableList<Any>>() {

    override fun isForViewType(items: MutableList<Any>, position: Int) =
            items[position] is CreateFolderModel

    override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        val itemView = parent.inflate(R.layout.item_create_bookmark_folder)
        itemView.createFolderButton.setOnClickListener {
            onCreateFolderClicked()
        }
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(
            items: MutableList<Any>,
            position: Int,
            viewHolder: RecyclerView.ViewHolder,
            payloads: MutableList<Any>
    ) = (viewHolder as ViewHolder).bind()

    private inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        fun bind() {

        }
    }

}