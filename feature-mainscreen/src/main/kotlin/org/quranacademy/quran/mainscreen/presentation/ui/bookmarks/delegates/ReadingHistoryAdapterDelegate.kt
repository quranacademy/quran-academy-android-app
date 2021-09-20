package org.quranacademy.quran.mainscreen.presentation.ui.bookmarks.delegates

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hannesdorfmann.adapterdelegates3.AdapterDelegate
import kotlinx.android.synthetic.main.item_recent_reading_place.view.*
import org.quranacademy.quran.bookmarks.domain.models.RecentReadingPlace
import org.quranacademy.quran.mainscreen.R
import org.quranacademy.quran.presentation.extensions.inflate
import javax.inject.Inject

class ReadingHistoryAdapterDelegate @Inject constructor(
        private val onClickListener: (RecentReadingPlace) -> Unit
) : AdapterDelegate<MutableList<Any>>() {

    override fun isForViewType(items: MutableList<Any>, position: Int) =
            items[position] is RecentReadingPlace

    override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder =
            ViewHolder(parent.inflate(R.layout.item_recent_reading_place))

    override fun onBindViewHolder(items: MutableList<Any>,
                                  position: Int,
                                  viewHolder: RecyclerView.ViewHolder,
                                  payloads: MutableList<Any>) =
            (viewHolder as ViewHolder).bind(items[position] as RecentReadingPlace)

    private inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        private lateinit var recentReadingPlace: RecentReadingPlace
        private val resources = view.context.resources

        init {
            view.setOnClickListener { onClickListener(recentReadingPlace) }
        }

        fun bind(recentReadingPlace: RecentReadingPlace) {
            this.recentReadingPlace = recentReadingPlace

            itemView.bookmarkTitle.text = if (recentReadingPlace.isMushafMode) {
                resources.getString(R.string.page_bookmark_template, recentReadingPlace.surahName, recentReadingPlace.pageNumber)
            } else {
                "${recentReadingPlace.surahName} ${recentReadingPlace.surahNumber}:${recentReadingPlace.ayahNumber}"
            }
        }

    }

}