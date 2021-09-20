package org.quranacademy.quran.translationsmanager.presentation.ui.translationordering.adapter

import android.view.ViewGroup
import com.thesurix.gesturerecycler.GestureAdapter
import com.thesurix.gesturerecycler.GestureViewHolder
import kotlinx.android.synthetic.main.item_translation.view.*
import kotlinx.android.synthetic.main.item_translation_category.view.*
import org.quranacademy.quran.presentation.extensions.inflate
import org.quranacademy.quran.translationsmanager.R
import org.quranacademy.quran.translationsmanager.presentation.mvp.global.TranslationCategory
import org.quranacademy.quran.translationsmanager.presentation.mvp.translationordering.TranslationOrderedUIModel

class TranslationsAdapter : GestureAdapter<Any, GestureViewHolder>() {

    companion object {
        const val TRANSLATION_ITEM = 0
        const val HEADER_ITEM = 1
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GestureViewHolder {
        return if (viewType == TRANSLATION_ITEM) {
            TranslationOrderViewHolder(parent.inflate(R.layout.item_translation_order, false))
        } else {
            TranslationOrderHeaderViewHolder(parent.inflate(R.layout.item_translation_category, false))
        }
    }

    override fun onBindViewHolder(holder: GestureViewHolder, position: Int) {
        super.onBindViewHolder(holder, position)

        val item = getItem(position)

        if (item is TranslationOrderedUIModel) {
            holder.itemView.translationTitle.text = item.name
            holder.itemView.translationInfo.text = item.languageName
        } else if (item is TranslationCategory) {
            holder.itemView.separatorText.text = item.title
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (getItem(position) is TranslationOrderedUIModel) TRANSLATION_ITEM else HEADER_ITEM
    }

}
