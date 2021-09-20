package org.quranacademy.quran.languagesmanager.presentation.ui

import android.util.Log
import androidx.recyclerview.widget.DiffUtil
import com.hannesdorfmann.adapterdelegates3.ListDelegationAdapter
import org.quranacademy.quran.languagesmanager.presentation.mvp.LanguageCategoryViewModel
import org.quranacademy.quran.languagesmanager.presentation.mvp.LanguageUIModel

class LanguagesAdapter(
        downloadClickListener: (LanguageUIModel) -> Unit,
        removeClickListener: (LanguageUIModel) -> Unit,
        enableClickListener: (LanguageUIModel) -> Unit
) : ListDelegationAdapter<MutableList<Any>>() {

    init {
        delegatesManager.addDelegate(LanguageCategoryAdapterDelegate())
        delegatesManager.addDelegate(LanguageAdapterDelegate(
                downloadClickListener,
                removeClickListener,
                enableClickListener
        ))
    }

    fun setData(translations: List<LanguageCategoryViewModel>) {
        val newItems = mutableListOf<Any>()
        translations.forEach {
            newItems.add(it)
            newItems.addAll(it.languages)
        }

        items = newItems
    }

    fun updateData(translations: List<LanguageCategoryViewModel>) {
        val newItems = mutableListOf<Any>()
        translations.forEach {
            newItems.add(it)
            newItems.addAll(it.languages)
        }

        val diffUtilCallback = LanguagesDiffUtils(items, newItems)
        val diffResult = DiffUtil.calculateDiff(diffUtilCallback)

        items = newItems
        diffResult.dispatchUpdatesTo(this)
    }

}