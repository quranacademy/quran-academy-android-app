package org.quranacademy.quran.presentation.ui.languagesystem.transformers.factories

import android.os.Build
import android.view.View
import android.widget.TextView
import org.quranacademy.quran.presentation.ui.languagesystem.transformers.*

@SuppressWarnings("NewApi")
object InternalViewTransformerFactory : ViewTransformerFactory {

    override fun getViewTransformer(view: View): ViewTransformer {
        val isLollipop = Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1
        return when {
            view is androidx.appcompat.widget.Toolbar -> SupportToolbarViewTransformer
            isLollipop && view is android.widget.Toolbar -> ToolbarViewTransformer
            view is TextView -> TextViewTransformer
            else -> EmptyViewTransformer
        }
    }

}