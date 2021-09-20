package org.quranacademy.quran.translationsmanager.presentation.ui.translationsmanager.adapter

import org.quranacademy.quran.presentation.ui.global.BaseDiffUtils
import org.quranacademy.quran.translationsmanager.domain.translations.TranslationUIModel
import org.quranacademy.quran.translationsmanager.presentation.mvp.global.TranslationCategory

class TranslationsDiffUtils(
        oldList: List<Any>,
        newList: List<Any>
) : BaseDiffUtils<Any>(oldList, newList) {

    override fun areItemsTheSame(old: Any, new: Any): Boolean {
        compare<TranslationUIModel>(old, new) { one, two ->
            return one.code == two.code
        }
        compare<TranslationCategory>(old, new) { one, two ->
            return one.title == two.title
        }
        return false
    }

    override fun areContentsTheSame(old: Any, new: Any): Boolean {
        compare<TranslationUIModel>(old, new) { one, two ->
            return one.isDownloaded == two.isDownloaded &&
                    one.isEnabled == two.isEnabled &&
                    one.isUpdateAvailable == two.isUpdateAvailable &&
                    one.isDownloading == two.isDownloading
        }
        compare<TranslationCategory>(old, new) { one, two ->
            return one.title == two.title
        }
        return false
    }

}