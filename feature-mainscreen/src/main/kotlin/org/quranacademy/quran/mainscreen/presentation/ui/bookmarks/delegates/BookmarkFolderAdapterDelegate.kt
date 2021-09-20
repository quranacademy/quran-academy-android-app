package org.quranacademy.quran.mainscreen.presentation.ui.bookmarks.delegates

import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.hannesdorfmann.adapterdelegates3.AdapterDelegate
import kotlinx.android.synthetic.main.item_bookmark_folder.view.*
import org.quranacademy.quran.mainscreen.R
import org.quranacademy.quran.mainscreen.presentation.mvp.bookmarkslist.BookmarkFolderUiModel
import org.quranacademy.quran.presentation.extensions.inflate
import org.quranacademy.quran.presentation.extensions.visible
import org.quranacademy.quran.presentation.ui.global.setDebounceOnClickLister
import org.quranacademy.quran.presentation.ui.global.toArabicNumberIfNeeded

class BookmarkFolderAdapterDelegate(
        private val onFolderClicked: (BookmarkFolderUiModel) -> Unit,
        private val onFolderOptionsClicked: (BookmarkFolderUiModel) -> Unit
) : AdapterDelegate<MutableList<Any>>() {

    override fun isForViewType(items: MutableList<Any>, position: Int) =
            items[position] is BookmarkFolderUiModel

    override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder =
            ViewHolder(parent.inflate(R.layout.item_bookmark_folder))

    override fun onBindViewHolder(
            items: MutableList<Any>,
            position: Int,
            viewHolder: RecyclerView.ViewHolder,
            payloads: MutableList<Any>
    ) = (viewHolder as ViewHolder).bind(items[position] as BookmarkFolderUiModel)

    private inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        fun bind(folder: BookmarkFolderUiModel) {
            val folderIcon = if (folder.isDefaultFolder) {
                R.drawable.ic_folder_special_black_24dp
            } else {
                R.drawable.ic_folder_black_24dp
            }
            itemView.folderIcon.setImageResource(folderIcon)
            itemView.folderName.text = folder.name
            itemView.bookmarksCountText.isVisible = folder.bookmarkCount > 0
            itemView.bookmarksCountText.text = "${folder.bookmarkCount.toArabicNumberIfNeeded()} -"
            itemView.folderOptionsButton.visible(!folder.isDefaultFolder)
            itemView.folderOptionsButton.setOnClickListener {
                onFolderOptionsClicked(folder)
            }
            itemView.setDebounceOnClickLister {
                onFolderClicked(folder)
            }
        }
    }

}