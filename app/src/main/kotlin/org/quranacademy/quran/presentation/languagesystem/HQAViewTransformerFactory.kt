package org.quranacademy.quran.presentation.languagesystem

import android.view.View
import org.quranacademy.quran.presentation.languagesystem.transformers.MaterialPreferenceCategoryTransformer
import org.quranacademy.quran.presentation.languagesystem.transformers.MaterialPreferenceTransformer
import org.quranacademy.quran.presentation.ui.languagesystem.transformers.ViewTransformer
import org.quranacademy.quran.presentation.ui.languagesystem.transformers.factories.ViewTransformerFactory
import org.quranacademy.quran.ui.preferences.AbsMaterialPreference
import org.quranacademy.quran.ui.preferences.MaterialPreferenceCategory

object HQAViewTransformerFactory : ViewTransformerFactory {

    override fun getViewTransformer(view: View): ViewTransformer? {
        return when (view) {
            is AbsMaterialPreference<*> -> MaterialPreferenceTransformer
            is MaterialPreferenceCategory -> MaterialPreferenceCategoryTransformer
            else -> null
        }
    }

}