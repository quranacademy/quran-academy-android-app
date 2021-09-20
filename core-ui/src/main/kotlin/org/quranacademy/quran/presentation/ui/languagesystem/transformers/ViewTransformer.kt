package org.quranacademy.quran.presentation.ui.languagesystem.transformers

import android.util.AttributeSet
import android.view.View

interface ViewTransformer {

    fun reword(view: View, attributeSet: AttributeSet)

    fun setTextIfExists(attributeSet: AttributeSet, index: Int, setTextResAction: (Int) -> Unit) {
        attributeSet.getAttributeResourceValue(index, -1).takeIf { it != -1 }?.let { setTextResAction(it) }
    }

}