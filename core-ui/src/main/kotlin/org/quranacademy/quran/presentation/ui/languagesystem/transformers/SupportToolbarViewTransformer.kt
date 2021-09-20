package org.quranacademy.quran.presentation.ui.languagesystem.transformers

import android.util.AttributeSet
import android.view.View
import androidx.appcompat.widget.Toolbar

internal object SupportToolbarViewTransformer : ViewTransformer {

    override fun reword(view: View, attributeSet: AttributeSet) {
        if (view is Toolbar) {
            attributeSet.forEach {
                when (attributeSet.getAttributeName(it)) {
                    "title" -> setTextIfExists(attributeSet, it, view::setTitle)
                    "subtitle" -> setTextIfExists(attributeSet, it, view::setSubtitle)
                }
            }
        }
    }

}