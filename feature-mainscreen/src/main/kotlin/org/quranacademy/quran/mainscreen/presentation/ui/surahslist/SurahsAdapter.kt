package org.quranacademy.quran.mainscreen.presentation.ui.surahslist

import com.hannesdorfmann.adapterdelegates3.ListDelegationAdapter
import org.quranacademy.quran.domain.models.Surah
import org.quranacademy.quran.mainscreen.presentation.mvp.surahslist.QuranJuz

class SurahsAdapter(
        onSurahClickListener: (Surah) -> Unit,
        onJuzClickListener: (juz: QuranJuz) -> Unit
) : ListDelegationAdapter<MutableList<Any>>() {

    init {
        items = mutableListOf()
        delegatesManager.addDelegate(SurahAdapterDelegate(onSurahClickListener))
        delegatesManager.addDelegate(QuranJuzAdapterDelegate(onJuzClickListener))
    }

    fun setData(surahsGroupedByJuz: List<QuranJuz>) {
        items.clear()
        surahsGroupedByJuz.forEach {
            items.add(it)
            items.addAll(it.surahs)
        }

        notifyDataSetChanged()
    }

}
