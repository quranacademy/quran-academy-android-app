package org.quranacademy.quran.ui.widgets.translationview.hqaformat

import android.R.attr.src
import android.content.Context
import android.graphics.Typeface
import android.text.Editable
import android.text.Spannable
import android.text.Spanned
import android.text.style.*
import androidx.core.content.ContextCompat
import org.quranacademy.quran.ui.htmlcompat.BaseHtmlTagHandler
import org.quranacademy.quran.ui.translationtextview.R
import org.quranacademy.quran.ui.widgets.translationview.hqaformat.spans.CustomFontSpan
import org.quranacademy.quran.ui.widgets.translationview.hqaformat.spans.FootnoteSpan
import org.xml.sax.Attributes
import org.xml.sax.XMLReader

class HQATranslationFormatTagHandler(
        private val context: Context
) : BaseHtmlTagHandler() {

    var onFootnoteClick: ((footnoteId: String) -> Unit)? = null

    private val ligaturesFont: Typeface by lazy {
        Typeface.createFromAsset(context.assets, "fonts/Ligatures.ttf")
    }
    private val arabicFont: Typeface by lazy {
        Typeface.createFromAsset(context.assets, context.getString(R.string.font_uthmanic_hafs))
    }

    override fun handleTag(opening: Boolean, tag: String, attributes: Attributes?, text: Editable, xmlReader: XMLReader) {
        if (opening) {
            when (tag) {
                "sas" -> handleLigature("A", text)
                "as" -> handleLigature("B", text)
                "radu" -> handleLigature("C", text)
                "rada" -> handleLigature("D", text)
                "radum" -> handleLigature("E", text)
                "raduma" -> handleLigature(" ", text) //ToDo: пока в шрифте нет лигатуры для raduma, поэтому вставляем пустоту
                "rahimu" -> handleLigature(" ", text)
                "rahimaha" -> handleLigature(" ", text)
                "rahimahum" -> handleLigature(" ", text)
                "footnote" -> handleFootnote(text, attributes)
                "ayah" -> handleLigature("\u06DD", text)
                "arabic" -> start(text, HQAFormatTag.Arabic())
                "quran-arabic" -> start(text, HQAFormatTag.Arabic())
                "hadith-arabic" -> start(text, HQAFormatTag.Arabic())
                "inline-comment" -> handleInlineComment(text)
                "quran-translation" -> handleTranslationStart(text)
                "hadith-translation" -> handleTranslationStart(text)
                "quran-source" -> handleSourceStart(text)
                "hadith-source" -> handleSourceStart(text)
            }
        } else {
            when (tag) {
                "arabic" -> end(text, HQAFormatTag.Arabic::class, CustomFontSpan(arabicFont))
                "quran-arabic" -> end(text, HQAFormatTag.Arabic::class, CustomFontSpan(arabicFont), RelativeSizeSpan(1.3f))
                "hadith-arabic" -> end(text, HQAFormatTag.Arabic::class, CustomFontSpan(arabicFont))
                "inline-comment" -> handleInlineCommentEnd(text)
                "quran-translation" -> handleTranslationEnd(text)
                "hadith-translation" -> handleTranslationEnd(text)
                "quran-source" -> handleSourceEnd(text)
                "hadith-source" -> handleSourceEnd(text)
            }
        }
    }

    private fun handleLigature(ligatureCode: String, text: Editable) {
        val len = text.length
        text.append(ligatureCode)
        text.setSpan(CustomFontSpan(ligaturesFont), len, len + ligatureCode.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
    }

    private fun handleFootnote(text: Editable, attributes: Attributes?) {
        requireNotNull(attributes) { "The footnote have not required attributes" }

        val footnoteId = attributes.getValue("", "id")
        val footnoteSymbol = "\uFFFC"
        val startSpan = text.length
        val endSpan = startSpan + footnoteSymbol.length
        text.append(footnoteSymbol)

        val footnoteClickSpan = FootnoteSpan { onFootnoteClick?.invoke(footnoteId) }
        text.setSpan(footnoteClickSpan, startSpan, endSpan, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        val footnoteIcon = context.resources.getDrawable(R.drawable.footnote_icon)
        footnoteIcon.setBounds(0, 0, footnoteIcon.intrinsicWidth, footnoteIcon.intrinsicHeight)
        val footnoteImageSpan = ImageSpan(footnoteIcon, src)
        text.setSpan(footnoteImageSpan, startSpan, endSpan, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
    }

    private fun handleInlineComment(text: Editable) {
        start(text, HQAFormatTag.InlineComment())
    }

    private fun handleInlineCommentEnd(text: Editable) {
        val color = ContextCompat.getColor(context, R.color.hqa_blue_75)
        end(text, HQAFormatTag.InlineComment::class, ForegroundColorSpan(color), ForegroundColorSpan(color))
    }

    private fun handleTranslationStart(text: Editable) {
        start(text, HQAFormatTag.TextTranslation())
    }

    private fun handleTranslationEnd(text: Editable) {
        end(text, HQAFormatTag.TextTranslation::class, StyleSpan(Typeface.ITALIC))
    }

    private fun handleSourceStart(text: Editable) {
        start(text, HQAFormatTag.TextSource())
    }

    private fun handleSourceEnd(text: Editable) {
        end(text, HQAFormatTag.TextSource::class, UnderlineSpan())
    }


}