package org.quranacademy.quran.surahdetails.ui

import android.annotation.SuppressLint
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hannesdorfmann.adapterdelegates3.AdapterDelegate
import kotlinx.android.synthetic.main.item_ayah_page_number_divider.view.*
import kotlinx.android.synthetic.main.item_surah_header.view.*
import org.quranacademy.quran.presentation.extensions.applyTypeface
import org.quranacademy.quran.presentation.extensions.inflate
import org.quranacademy.quran.presentation.extensions.visible
import org.quranacademy.quran.presentation.ui.global.getCurrentAppLanguage
import org.quranacademy.quran.presentation.ui.global.isArabic
import org.quranacademy.quran.presentation.ui.global.toArabicNumberIfNeeded
import org.quranacademy.quran.surahdetails.R
import org.quranacademy.quran.surahdetails.mvp.SurahDetailsUiModel

class SurahHeaderAdapterDelegate : AdapterDelegate<MutableList<Any>>() {

    private val isCurrentArabicLanguage = getCurrentAppLanguage().isArabic()

    override fun isForViewType(items: MutableList<Any>, position: Int) =
            items[position] is SurahDetailsUiModel

    override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        val itemView = parent.inflate(R.layout.item_surah_header)
        if (isCurrentArabicLanguage) {
            itemView.surahName.applyTypeface(R.string.font_kitab)
        }
        return ViewHolder(itemView)
    }


    override fun onBindViewHolder(items: MutableList<Any>,
                                  position: Int,
                                  viewHolder: RecyclerView.ViewHolder,
                                  payloads: MutableList<Any>) =
            (viewHolder as ViewHolder).bind(items[position] as SurahDetailsUiModel)

    private inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        private val context = view.context
        private lateinit var surah: SurahDetailsUiModel

        @SuppressLint("SetTextI18n")
        fun bind(surah: SurahDetailsUiModel) {
            this.surah = surah

            if (surah.showPageNumber) {
                itemView.ayahPageNumberContainer.visible(true)
                itemView.ayahPageNumber.text = context.getString(
                        R.string.page_number_template,
                        surah.pageNumber.toArabicNumberIfNeeded(isCurrentArabicLanguage)
                )
            }

            itemView.surahName.text = getSurahName(surah)
            itemView.surahInfo.text = context.getString(
                    R.string.surah_info_template,
                    surah.translatedName,
                    surah.ayahsCount.toArabicNumberIfNeeded()
            )
            itemView.bislmillah.visible(surah.bismillahPre)
        }

        private fun getSurahName(surah: SurahDetailsUiModel): String {
            return if (isCurrentArabicLanguage) {
                val surahNumber = surah.surahNumber.toArabicNumberIfNeeded(isCurrentArabicLanguage)
                "$surahNumber - ${surah.transliteratedName}"
            } else {
                "${surah.surahNumber}. ${surah.transliteratedName}"
            }
        }


    }

}