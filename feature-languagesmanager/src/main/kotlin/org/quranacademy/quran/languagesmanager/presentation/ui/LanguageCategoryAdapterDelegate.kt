package org.quranacademy.quran.languagesmanager.presentation.ui

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hannesdorfmann.adapterdelegates3.AdapterDelegate
import kotlinx.android.synthetic.main.item_language_category.view.*
import org.quranacademy.quran.languagesmanager.R
import org.quranacademy.quran.languagesmanager.presentation.mvp.LanguageCategoryViewModel
import org.quranacademy.quran.presentation.extensions.inflate

class LanguageCategoryAdapterDelegate : AdapterDelegate<MutableList<Any>>() {

    override fun isForViewType(items: MutableList<Any>, position: Int) =
            items[position] is LanguageCategoryViewModel

    override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder =
            ViewHolder(parent.inflate(R.layout.item_language_category))

    override fun onBindViewHolder(items: MutableList<Any>,
                                  position: Int,
                                  viewHolder: RecyclerView.ViewHolder,
                                  payloads: MutableList<Any>) =
            (viewHolder as ViewHolder).bind(items[position] as LanguageCategoryViewModel)

    private inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        private lateinit var category: LanguageCategoryViewModel

        fun bind(category: LanguageCategoryViewModel) {
            this.category = category

            itemView.separatorText.text = category.name
        }
    }

}