package org.quranacademy.quran.wordbywordmanager.presentation.ui

import org.quranacademy.quran.presentation.ui.global.BaseDiffUtils
import org.quranacademy.quran.wordbywordmanager.domain.WordByWordTranslationUIModel

class WordByWordTranslationsDiffUtils(
        oldList: List<Any>, newList: List<Any>
) : BaseDiffUtils<Any>(oldList, newList) {

    override fun areItemsTheSame(old: Any, new: Any): Boolean {
        compare<WordByWordTranslationUIModel>(old, new) { one, two ->
            return one.id == two.id
        }
        compare<WordByWordTranslationCategory>(old, new) { one, two ->
            return one.name == two.name
        }
        return false
    }

    override fun areContentsTheSame(old: Any, new: Any): Boolean {
        compare<WordByWordTranslationUIModel>(old, new) { one, two ->
            return one.name == two.name &&
                    one.isDownloaded == two.isDownloaded &&
                    one.isEnabled == two.isEnabled &&
                    one.isUpdateAvailable == two.isUpdateAvailable
        }
        compare<WordByWordTranslationCategory>(old, new) { one, two ->
            return one.name == two.name
        }
        return false
    }

}