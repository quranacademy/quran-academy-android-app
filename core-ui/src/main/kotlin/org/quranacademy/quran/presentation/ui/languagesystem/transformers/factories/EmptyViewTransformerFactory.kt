package org.quranacademy.quran.presentation.ui.languagesystem.transformers.factories

import android.view.View
import org.quranacademy.quran.presentation.ui.languagesystem.transformers.ViewTransformer

object EmptyViewTransformerFactory : ViewTransformerFactory {

    override fun getViewTransformer(view: View): ViewTransformer? = null

}