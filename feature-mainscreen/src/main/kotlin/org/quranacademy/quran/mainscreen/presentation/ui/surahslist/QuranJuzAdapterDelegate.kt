package org.quranacademy.quran.mainscreen.presentation.ui.surahslist

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hannesdorfmann.adapterdelegates3.AdapterDelegate
import kotlinx.android.synthetic.main.item_juz.view.*
import org.quranacademy.quran.mainscreen.R
import org.quranacademy.quran.mainscreen.presentation.mvp.surahslist.QuranJuz
import org.quranacademy.quran.presentation.extensions.inflate
import org.quranacademy.quran.presentation.ui.global.getCurrentAppLanguage
import org.quranacademy.quran.presentation.ui.global.isArabic
import org.quranacademy.quran.presentation.ui.global.toArabicNumberIfNeeded

class QuranJuzAdapterDelegate(
        val onJuzClickListener: (juz: QuranJuz) -> Unit
) : AdapterDelegate<MutableList<Any>>() {

    private val isCurrentArabicLanguage = getCurrentAppLanguage().isArabic()

    override fun isForViewType(items: MutableList<Any>, position: Int) =
            items[position] is QuranJuz

    override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder =
            ViewHolder(parent.inflate(R.layout.item_juz))

    override fun onBindViewHolder(items: MutableList<Any>,
                                  position: Int,
                                  viewHolder: RecyclerView.ViewHolder,
                                  payloads: MutableList<Any>) =
            (viewHolder as ViewHolder).bind(items[position] as QuranJuz)

    private inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        private val context = view.context
        private val resources = context.resources

        fun bind(juz: QuranJuz) {
            itemView.juzTitle.text = resources.getString(R.string.juz_title, juz.number)
            itemView.pageNumber.text = juz.startPageNumber.toArabicNumberIfNeeded(isCurrentArabicLanguage)
            itemView.setOnClickListener { onJuzClickListener(juz) }
        }
    }

}