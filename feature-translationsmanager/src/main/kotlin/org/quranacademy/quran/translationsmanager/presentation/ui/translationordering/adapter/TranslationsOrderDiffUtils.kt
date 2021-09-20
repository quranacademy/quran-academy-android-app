package org.quranacademy.quran.translationsmanager.presentation.ui.translationordering.adapter

import org.quranacademy.quran.presentation.ui.global.BaseDiffUtils
import org.quranacademy.quran.translationsmanager.presentation.mvp.global.TranslationCategory
import org.quranacademy.quran.translationsmanager.presentation.mvp.global.TranslationItem
import org.quranacademy.quran.translationsmanager.presentation.mvp.translationordering.TranslationOrderedUIModel

class TranslationsOrderDiffUtils(
        oldList: List<TranslationItem>,
        newList: List<TranslationItem>
) : BaseDiffUtils<Any>(oldList, newList) {

    override fun areItemsTheSame(old: Any, new: Any): Boolean {
        compare<TranslationOrderedUIModel>(old, new) { one, two ->
            return one.translationCode == two.translationCode
        }
        compare<TranslationCategory>(old, new) { one, two ->
            return one.title == two.title
        }
        return false
    }

    override fun areContentsTheSame(old: Any, new: Any): Boolean {
        compare<TranslationOrderedUIModel>(old, new) { one, two ->
            return one.translationCode == two.translationCode
        }
        compare<TranslationCategory>(old, new) { one, two ->
            return one.title == two.title
        }
        return false
    }

}