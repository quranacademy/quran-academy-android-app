package org.quranacademy.quran.surahdetails.ui

import android.annotation.SuppressLint
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hannesdorfmann.adapterdelegates3.AdapterDelegate
import kotlinx.android.synthetic.main.item_surah_footer.view.*
import org.quranacademy.quran.presentation.extensions.inflate
import org.quranacademy.quran.surahdetails.R

class SurahFooterAdapterDelegate(
        private val onNextSurahClickListener: () -> Unit
) : AdapterDelegate<MutableList<Any>>() {

    class Item

    override fun isForViewType(items: MutableList<Any>, position: Int) =
            items[position] is SurahFooterAdapterDelegate.Item

    override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder =
            ViewHolder(parent.inflate(R.layout.item_surah_footer))

    override fun onBindViewHolder(items: MutableList<Any>,
                                  position: Int,
                                  viewHolder: RecyclerView.ViewHolder,
                                  payloads: MutableList<Any>) =
            (viewHolder as ViewHolder).bind()

    private inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        @SuppressLint("SetTextI18n")
        fun bind() {

            itemView.openNextSurahButton.setOnClickListener {
                onNextSurahClickListener()
            }
        }

    }

}