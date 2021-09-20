package org.quranacademy.quran.presentation.languagesystem.transformers

import android.util.AttributeSet
import android.view.View
import org.quranacademy.quran.presentation.ui.languagesystem.transformers.ViewTransformer
import org.quranacademy.quran.presentation.ui.languagesystem.transformers.forEach
import org.quranacademy.quran.ui.preferences.AbsMaterialPreference

internal object MaterialPreferenceTransformer : ViewTransformer {

    override fun reword(view: View, attributeSet: AttributeSet) {
        if (view is AbsMaterialPreference<*>) {
            attributeSet.forEach {
                when (attributeSet.getAttributeName(it)) {
                    "mp_title" -> setTextIfExists(attributeSet, it, view::setTitle)
                    "mp_summary" -> setTextIfExists(attributeSet, it, view::setSummary)
                }
            }
        }
    }

}