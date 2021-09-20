package org.quranacademy.quran.translationsmanager.presentation.ui.translationsmanager.adapter

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hannesdorfmann.adapterdelegates3.AdapterDelegate
import kotlinx.android.synthetic.main.item_translation_category.view.*
import org.quranacademy.quran.presentation.extensions.inflate
import org.quranacademy.quran.translationsmanager.R
import org.quranacademy.quran.translationsmanager.presentation.mvp.global.TranslationCategory

class TranslationCategoryAdapterDelegate : AdapterDelegate<MutableList<Any>>() {

    override fun isForViewType(items: MutableList<Any>, position: Int) =
            items[position] is TranslationCategory

    override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder =
            ViewHolder(parent.inflate(R.layout.item_translation_category))

    override fun onBindViewHolder(items: MutableList<Any>,
                                  position: Int,
                                  viewHolder: RecyclerView.ViewHolder,
                                  payloads: MutableList<Any>) =
            (viewHolder as ViewHolder).bind(items[position] as TranslationCategory)

    private inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        private lateinit var category: TranslationCategory

        fun bind(category: TranslationCategory) {
            this.category = category

            itemView.separatorText.text = category.title
        }
    }

}