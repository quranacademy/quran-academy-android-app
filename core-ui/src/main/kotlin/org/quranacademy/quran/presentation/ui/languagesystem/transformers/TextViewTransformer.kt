package org.quranacademy.quran.presentation.ui.languagesystem.transformers

import android.util.AttributeSet
import android.view.View
import android.widget.TextView

internal object TextViewTransformer : ViewTransformer {

    override fun reword(view: View, attributeSet: AttributeSet) {
        if (view is TextView) {
            attributeSet.forEach {
                when (attributeSet.getAttributeName(it)) {
                    "text" -> setTextIfExists(attributeSet, it, view::setText)
                    "hint" -> setTextIfExists(attributeSet, it, view::setHint)
                }
            }
        }
    }

}