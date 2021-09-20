package org.quranacademy.quran.wordbywordmanager.presentation.ui

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hannesdorfmann.adapterdelegates3.AdapterDelegate
import kotlinx.android.synthetic.main.item_word_by_word_translation_category.view.*
import org.quranacademy.quran.presentation.extensions.inflate
import org.quranacademy.quran.wordbywordmanager.R

class WordByWordTranslationCategoryAdapterDelegate : AdapterDelegate<MutableList<Any>>() {

    override fun isForViewType(items: MutableList<Any>, position: Int) =
            items[position] is WordByWordTranslationCategory

    override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder =
            ViewHolder(parent.inflate(R.layout.item_word_by_word_translation_category))

    override fun onBindViewHolder(
            items: MutableList<Any>,
            position: Int,
            viewHolder: RecyclerView.ViewHolder,
            payloads: MutableList<Any>
    ) = (viewHolder as ViewHolder).bind(items[position] as WordByWordTranslationCategory)

    private inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        private lateinit var category: WordByWordTranslationCategory

        fun bind(category: WordByWordTranslationCategory) {
            this.category = category

            itemView.separatorText.text = category.name
        }
    }

}