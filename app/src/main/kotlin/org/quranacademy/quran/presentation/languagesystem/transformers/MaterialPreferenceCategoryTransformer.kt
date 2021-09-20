package org.quranacademy.quran.presentation.languagesystem.transformers

import android.util.AttributeSet
import android.view.View
import org.quranacademy.quran.presentation.ui.languagesystem.transformers.ViewTransformer
import org.quranacademy.quran.presentation.ui.languagesystem.transformers.forEach
import org.quranacademy.quran.ui.preferences.MaterialPreferenceCategory

internal object MaterialPreferenceCategoryTransformer : ViewTransformer {

    override fun reword(view: View, attributeSet: AttributeSet) {
        if (view is MaterialPreferenceCategory) {
            attributeSet.forEach {
                when (attributeSet.getAttributeName(it)) {
                    "mpc_title" -> setTextIfExists(attributeSet, it, view::setTitle)
                }
            }
        }
    }

}