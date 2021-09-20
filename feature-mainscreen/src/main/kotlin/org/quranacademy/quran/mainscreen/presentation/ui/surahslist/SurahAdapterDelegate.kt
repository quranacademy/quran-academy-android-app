package org.quranacademy.quran.mainscreen.presentation.ui.surahslist

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hannesdorfmann.adapterdelegates3.AdapterDelegate
import kotlinx.android.synthetic.main.item_surah.view.*
import org.quranacademy.quran.domain.models.Surah
import org.quranacademy.quran.mainscreen.R
import org.quranacademy.quran.presentation.extensions.applyTypeface
import org.quranacademy.quran.presentation.extensions.inflate
import org.quranacademy.quran.presentation.ui.global.getCurrentAppLanguage
import org.quranacademy.quran.presentation.ui.global.isArabic
import org.quranacademy.quran.presentation.ui.global.toArabicNumberIfNeeded

class SurahAdapterDelegate(
        private val onClickListener: (Surah) -> Unit
) : AdapterDelegate<MutableList<Any>>() {

    private val isCurrentArabicLanguage = getCurrentAppLanguage().isArabic()

    override fun isForViewType(items: MutableList<Any>, position: Int) =
            items[position] is Surah

    override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        val itemView = parent.inflate(R.layout.item_surah)
        if (isCurrentArabicLanguage) {
            itemView.surahTitle.applyTypeface(R.string.font_kitab)
            itemView.pageNumber.applyTypeface(R.string.font_kitab)
        }
        return ViewHolder(itemView)
    }


    override fun onBindViewHolder(items: MutableList<Any>,
                                  position: Int,
                                  viewHolder: RecyclerView.ViewHolder,
                                  payloads: MutableList<Any>) =
            (viewHolder as ViewHolder).bind(items[position] as Surah)

    private inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        private val context = view.context
        private val resources = context.resources
        private lateinit var surah: Surah

        init {
            view.setOnClickListener { onClickListener(surah) }
        }

        fun bind(surah: Surah) {
            this.surah = surah

            val ayahsCount = surah.ayahsCount
            val surahDescription = getSurahDescription(surah, ayahsCount)

            itemView.surahNumber.text = surah.surahNumber.toArabicNumberIfNeeded(isCurrentArabicLanguage)
            itemView.surahTitle.text = surah.transliteratedName
            itemView.surahDescription.text = surahDescription
            itemView.pageNumber.text = surah.pageNumber.toArabicNumberIfNeeded(isCurrentArabicLanguage)
        }

        private fun getSurahDescription(surah: Surah, ayahsCount: Int): String {
            return if (isCurrentArabicLanguage) {
                resources.getQuantityString(R.plurals.ayahs_count_label, ayahsCount, ayahsCount)
            } else {
                context.getString(
                        R.string.surah_description_template,
                        surah.translatedName,
                        resources.getQuantityString(R.plurals.ayahs_count_label, ayahsCount, ayahsCount)
                )
            }
        }
    }

}