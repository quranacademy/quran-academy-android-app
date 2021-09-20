package org.quranacademy.quran.mainscreen.presentation.ui.bookmarks.delegates

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hannesdorfmann.adapterdelegates3.AdapterDelegate
import kotlinx.android.synthetic.main.item_page_bookmark.view.*
import org.quranacademy.quran.bookmarks.domain.models.PageBookmark
import org.quranacademy.quran.mainscreen.R
import org.quranacademy.quran.presentation.extensions.inflate
import javax.inject.Inject

class PageBookmarkAdapterDelegate @Inject constructor(
        private val onClickListener: (PageBookmark) -> Unit,
        private val onLongClickListener: (PageBookmark) -> Unit,
        private val isItemSelected: (PageBookmark) -> Boolean
) : AdapterDelegate<MutableList<Any>>() {

    override fun isForViewType(items: MutableList<Any>, position: Int) =
            items[position] is PageBookmark

    override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder =
            ViewHolder(parent.inflate(R.layout.item_page_bookmark))

    override fun onBindViewHolder(items: MutableList<Any>,
                                  position: Int,
                                  viewHolder: RecyclerView.ViewHolder,
                                  payloads: MutableList<Any>) =
            (viewHolder as ViewHolder).bind(items[position] as PageBookmark)

    private inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        private val context = view.context
        private val resources = context.resources
        private lateinit var pageBookmark: PageBookmark

        init {
            view.setOnClickListener { onClickListener(pageBookmark) }
            view.setOnLongClickListener { onLongClickListener(pageBookmark); true }
        }

        fun bind(pageBookmark: PageBookmark) {
            this.pageBookmark = pageBookmark

            val surahName = pageBookmark.surahName
            val pageNumber = pageBookmark.pageNumber

            itemView.bookmarkTitle.text = resources.getString(R.string.page_bookmark_template, surahName, pageNumber)
            itemView.bookmarkItem.isSelected = isItemSelected(pageBookmark)
        }
    }

}