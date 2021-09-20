package org.quranacademy.quran.ui.widgets.translationview

import android.content.Context
import android.text.method.LinkMovementMethod
import android.util.AttributeSet
import android.widget.TextView
import org.quranacademy.quran.ui.htmlcompat.HtmlCompat
import org.quranacademy.quran.ui.widgets.translationview.hqaformat.HQATranslationFormatTagHandler

class FootnoteTextView @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyle: Int = 0
) : TextView(context, attrs, defStyle) {

    init {
        movementMethod = LinkMovementMethod.getInstance()
    }

    fun setHtmlText(footnoteText: String) {
        val tagHandler = HQATranslationFormatTagHandler(context)
        text = HtmlCompat.fromHtml(context, footnoteText, 0, null, tagHandler)
    }

}