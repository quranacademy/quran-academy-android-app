package org.quranacademy.quran.ui.htmlcompat

import android.text.Editable
import android.text.Spannable
import android.text.Spanned
import kotlin.reflect.KClass

abstract class BaseHtmlTagHandler : HtmlCompat.TagHandler {

    protected fun start(text: Editable, mark: Any) {
        val len = text.length
        text.setSpan(mark, len, len, Spannable.SPAN_MARK_MARK)
    }

    protected fun end(text: Editable, kind: KClass<*>, vararg replaces: Any) {
        val len = text.length
        val obj = getLast(text, kind)
        val where = text.getSpanStart(obj)

        text.removeSpan(obj)

        if (where >= 0 && where != len) {
            for (replace in replaces) {
                text.setSpan(replace, where, len, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            }
        }
    }

    private fun <T : Any> getLast(text: Spanned, kind: KClass<T>): T? {
        /*
        * This knows that the last returned object from getSpans()
        * will be the most recently added.
        */
        val objects = text.getSpans(0, text.length, kind.java)
        return if (objects.isEmpty()) {
            null
        } else {
            objects[objects.size - 1]
        }
    }

}