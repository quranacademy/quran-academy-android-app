package org.quranacademy.quran.languagesmanager.presentation.ui

import org.quranacademy.quran.languagesmanager.presentation.mvp.LanguageCategoryViewModel
import org.quranacademy.quran.languagesmanager.presentation.mvp.LanguageUIModel
import org.quranacademy.quran.presentation.ui.global.BaseDiffUtils

class LanguagesDiffUtils(
        oldList: List<Any>,
        newList: List<Any>
) : BaseDiffUtils<Any>(oldList, newList) {

    override fun areItemsTheSame(old: Any, new: Any): Boolean {
        compare<LanguageCategoryViewModel>(old, new) { one, two ->
            return one.name == two.name
        }
        compare<LanguageUIModel>(old, new) { one, two ->
            return one.code == two.code
        }
        return false
    }

    override fun areContentsTheSame(old: Any, new: Any): Boolean {
        compare<LanguageUIModel>(old, new) { one, two ->
            return one.name == two.name &&
                    one.isDownloaded == two.isDownloaded &&
                    one.isEnabled == two.isEnabled
        }
        compare<LanguageCategoryViewModel>(old, new) { one, two ->
            return one.name == two.name
        }
        return false
    }

}