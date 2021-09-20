package org.quranacademy.quran.translationsmanager.presentation.ui.translationsmanager.adapter

import android.content.Context
import androidx.recyclerview.widget.DiffUtil
import com.hannesdorfmann.adapterdelegates3.ListDelegationAdapter
import org.quranacademy.quran.core.ui.R
import org.quranacademy.quran.domain.models.FileDownloadInfo
import org.quranacademy.quran.translationsmanager.domain.translations.TranslationUIModel
import org.quranacademy.quran.translationsmanager.presentation.mvp.global.TranslationCategory

class TranslationsAdapter(
        private val context: Context,
        downloadClickListener: (TranslationUIModel) -> Unit,
        onCancelDownloadingClicked: (TranslationUIModel) -> Unit,
        updateClickListener: (TranslationUIModel) -> Unit,
        removeClickListener: (TranslationUIModel) -> Unit,
        enableClickListener: (TranslationUIModel, Boolean) -> Unit
) : ListDelegationAdapter<MutableList<Any>>() {

    private val translationAdapterDelegate = TranslationAdapterDelegate(
            onDownloadTranslationClicked = downloadClickListener,
            onCancelDownloadingClicked = onCancelDownloadingClicked,
            removeClickListener = removeClickListener,
            updateClickListener = updateClickListener,
            onEnableTranslationClicked = enableClickListener
    )

    init {
        delegatesManager.addDelegate(TranslationCategoryAdapterDelegate())
        delegatesManager.addDelegate(translationAdapterDelegate)
    }

    fun setData(translations: List<TranslationUIModel>, splitByCategory: Boolean) {
        val newItems: List<Any> = if (splitByCategory) {
            getTranslationsSplitByCategory(translations)
        } else {
            translations
        }
        items = newItems.toMutableList()
        notifyDataSetChanged()
    }

    fun updateDownloadProgress(translation: TranslationUIModel, downloadInfo: FileDownloadInfo?) {
        translationAdapterDelegate.updateDownloadProgress(translation, downloadInfo)
    }

    fun updateData(translations: List<TranslationUIModel>, splitByCategory: Boolean) {
        val newItems: List<Any> = if (splitByCategory) {
            getTranslationsSplitByCategory(translations)
        } else {
            translations
        }

        val diffUtilCallback = TranslationsDiffUtils(items, newItems)
        val diffResult = DiffUtil.calculateDiff(diffUtilCallback)

        items = newItems.toMutableList()
        diffResult.dispatchUpdatesTo(this)
    }

    private fun getTranslationsSplitByCategory(translations: List<TranslationUIModel>): List<Any> {
        val splitTranslations = mutableListOf<Any>()

        val resources = context.resources
        val downloadedTitle = resources.getString(R.string.downloaded_category_label)
        val downloadedTranslations = translations.filter { it.isDownloaded }
        val downloadedCategory = TranslationCategory(downloadedTitle)
        splitTranslations.add(downloadedCategory)
        splitTranslations.addAll(downloadedTranslations)

        val availableTitle = resources.getString(R.string.available_category_label)
        val availableCategory = TranslationCategory(availableTitle)
        val availableTranslations = translations.filter { !it.isDownloaded }
        splitTranslations.add(availableCategory)
        splitTranslations.addAll(availableTranslations)
        return splitTranslations
    }

}
