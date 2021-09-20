package org.quranacademy.quran.wordbywordmanager.presentation.ui

import android.content.Context
import androidx.recyclerview.widget.DiffUtil
import com.hannesdorfmann.adapterdelegates3.ListDelegationAdapter
import org.quranacademy.quran.core.ui.R
import org.quranacademy.quran.wordbywordmanager.domain.WordByWordTranslationUIModel

class WordByWordTranslationsAdapter(
        private val context: Context,
        downloadClickListener: (WordByWordTranslationUIModel) -> Unit,
        updateClickListener: (WordByWordTranslationUIModel) -> Unit,
        deleteClickListener: (WordByWordTranslationUIModel) -> Unit,
        enableClickListener: (WordByWordTranslationUIModel) -> Unit
) : ListDelegationAdapter<MutableList<Any>>() {

    init {
        delegatesManager.addDelegate(WordByWordTranslationCategoryAdapterDelegate())
        delegatesManager.addDelegate(WordByWordTranslationAdapterDelegate(
                downloadClickListener = downloadClickListener,
                deleteClickListener = deleteClickListener,
                enableClickListener = enableClickListener,
                updateClickListener = updateClickListener
        ))
    }

    fun setData(translations: List<WordByWordTranslationUIModel>, splitByCategory: Boolean) {
        val newItems: List<Any> = if (splitByCategory) {
            getTranslationsSplitByCategory(translations)
        } else {
            translations
        }

        items = newItems.toMutableList()
        notifyDataSetChanged()
    }

    fun updateData(translations: List<WordByWordTranslationUIModel>, splitByCategory: Boolean) {
        val newItems: List<Any> = if (splitByCategory) {
            getTranslationsSplitByCategory(translations)
        } else {
            translations
        }

        val diffUtilCallback = WordByWordTranslationsDiffUtils(items, newItems)
        val diffResult = DiffUtil.calculateDiff(diffUtilCallback)

        items = newItems.toMutableList()
        diffResult.dispatchUpdatesTo(this)
    }

    private fun getTranslationsSplitByCategory(translations: List<WordByWordTranslationUIModel>): List<Any> {
        val splitTranslations = mutableListOf<Any>()

        val resources = context.resources
        val downloadedTitle = resources.getString(R.string.downloaded_category_label)
        val downloadedTranslations = translations.filter { it.isDownloaded }
        val downloadedCategory = WordByWordTranslationCategory(downloadedTitle)
        splitTranslations.add(downloadedCategory)
        splitTranslations.addAll(downloadedTranslations)

        val availableTitle = resources.getString(R.string.available_category_label)
        val availableCategory = WordByWordTranslationCategory(availableTitle)
        val availableTranslations = translations.filter { !it.isDownloaded }
        splitTranslations.add(availableCategory)
        splitTranslations.addAll(availableTranslations)
        return splitTranslations
    }

}