package org.quranacademy.quran.mainscreen.presentation.ui.bookmarks.delegates

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hannesdorfmann.adapterdelegates3.AdapterDelegate
import kotlinx.android.synthetic.main.item_bookmark_category.view.*
import org.quranacademy.quran.mainscreen.R
import org.quranacademy.quran.mainscreen.presentation.mvp.bookmarkslist.BookmarkCategory
import org.quranacademy.quran.presentation.extensions.inflate

class BookmarkCategoryAdapterDelegate : AdapterDelegate<MutableList<Any>>() {

    override fun isForViewType(items: MutableList<Any>, position: Int) =
            items[position] is BookmarkCategory

    override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder =
            ViewHolder(parent.inflate(R.layout.item_bookmark_category))

    override fun onBindViewHolder(items: MutableList<Any>,
                                  position: Int,
                                  viewHolder: RecyclerView.ViewHolder,
                                  payloads: MutableList<Any>) =
            (viewHolder as ViewHolder).bind(items[position] as BookmarkCategory)

    private inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        fun bind(category: BookmarkCategory) {
            itemView.categoryTitle.text = category.title
        }
    }

}