package org.quranacademy.quran.surahdetails.ui

import android.content.Context
import com.hannesdorfmann.adapterdelegates3.ListDelegationAdapter
import com.simplecityapps.recyclerview_fastscroll.views.FastScrollRecyclerView
import kotlinx.coroutines.CoroutineScope
import org.quranacademy.quran.QuranConstants
import org.quranacademy.quran.data.prefs.AppPreferences
import org.quranacademy.quran.domain.models.Ayah
import org.quranacademy.quran.presentation.ui.appearance.AppearanceManager
import org.quranacademy.quran.surahdetails.mvp.AyahUiModel
import org.quranacademy.quran.surahdetails.mvp.SurahDetailsUiModel

class SurahAyahsAdapter(
        context: Context,
        coroutineScope: CoroutineScope,
        appearanceManager: AppearanceManager,
        appPreferences: AppPreferences,
        onAyahClickListener: (AyahUiModel) -> Unit,
        onNextSurahClickListener: () -> Unit
) : ListDelegationAdapter<MutableList<Any>>(), FastScrollRecyclerView.SectionedAdapter {

    private val surahAyahAdapterDelegate = AyahAdapterDelegate(
            context = context,
            onClickListener = onAyahClickListener,
            appearanceManager = appearanceManager,
            appPreferences = appPreferences,
            coroutineScope = coroutineScope
    )

    init {
        items = mutableListOf()
        delegatesManager.addDelegate(SurahHeaderAdapterDelegate())
        delegatesManager.addDelegate(surahAyahAdapterDelegate)
        delegatesManager.addDelegate(SurahFooterAdapterDelegate(onNextSurahClickListener))
    }

    fun setData(surah: SurahDetailsUiModel) {
        items.clear()
        items.add(surah)
        items.addAll(surah.ayahs)

        val isLastSurah = surah.surahNumber == QuranConstants.SURAHS_COUNT
        if (!isLastSurah) {
            items.add(SurahFooterAdapterDelegate.Item())
        }

        notifyDataSetChanged()
    }

    fun highlightNowPlayingAyah(ayahNumber: Int?) {
        val prevAyah = surahAyahAdapterDelegate.nowPlayingAyahNumber
        surahAyahAdapterDelegate.highlightNowPlayingAyah(ayahNumber)
        prevAyah?.let { notifyItemChanged(it) }
        ayahNumber?.let { notifyItemChanged(it) }
    }

    fun highlightNowPlayingWord(wordNumber: Int?) {
        surahAyahAdapterDelegate.highlightNowPlayingWord(wordNumber)
        surahAyahAdapterDelegate.nowPlayingAyahNumber?.let {
            notifyItemChanged(it)
        }
    }

    override fun getSectionName(position: Int): String {
        val item = items[position]
        if (item is Ayah) {
            return item.ayahNumber.toString()
        }
        return ""
    }

}
