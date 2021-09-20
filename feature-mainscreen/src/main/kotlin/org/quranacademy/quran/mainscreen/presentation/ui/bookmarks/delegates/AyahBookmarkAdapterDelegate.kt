package org.quranacademy.quran.mainscreen.presentation.ui.bookmarks.delegates

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hannesdorfmann.adapterdelegates3.AdapterDelegate
import kotlinx.android.synthetic.main.item_bookmark.view.*
import org.quranacademy.quran.bookmarks.domain.models.AyahBookmark
import org.quranacademy.quran.mainscreen.R
import org.quranacademy.quran.presentation.extensions.inflate
import javax.inject.Inject

class AyahBookmarkAdapterDelegate @Inject constructor(
        private val onClickListener: (AyahBookmark) -> Unit,
        private val onLongClickListener: (AyahBookmark) -> Unit,
        private val isItemSelected: (AyahBookmark) -> Boolean
) : AdapterDelegate<MutableList<Any>>() {

    override fun isForViewType(items: MutableList<Any>, position: Int) =
            items[position] is AyahBookmark

    override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder =
            ViewHolder(parent.inflate(R.layout.item_bookmark))

    override fun onBindViewHolder(items: MutableList<Any>,
                                  position: Int,
                                  viewHolder: RecyclerView.ViewHolder,
                                  payloads: MutableList<Any>) =
            (viewHolder as ViewHolder).bind(items[position] as AyahBookmark)

    private inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        private lateinit var ayahBookmark: AyahBookmark

        init {
            view.setOnClickListener { onClickListener(ayahBookmark) }
            view.setOnLongClickListener { onLongClickListener(ayahBookmark); true }
        }

        fun bind(ayahBookmark: AyahBookmark) {
            this.ayahBookmark = ayahBookmark

            itemView.bookmarkTitle.text = "${ayahBookmark.surahName} ${ayahBookmark.surahNumber}:${ayahBookmark.ayahNumber}"
            itemView.bookmarkItem.isSelected = isItemSelected(ayahBookmark)
        }
    }

}