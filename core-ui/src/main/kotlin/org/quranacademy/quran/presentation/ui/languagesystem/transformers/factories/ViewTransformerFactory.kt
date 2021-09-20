package org.quranacademy.quran.presentation.ui.languagesystem.transformers.factories

import android.view.View
import org.quranacademy.quran.presentation.ui.languagesystem.transformers.ViewTransformer

interface ViewTransformerFactory {
    fun getViewTransformer(view: View): ViewTransformer?
}