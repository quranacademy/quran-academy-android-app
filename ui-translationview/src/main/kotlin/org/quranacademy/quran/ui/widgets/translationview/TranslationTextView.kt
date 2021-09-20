package org.quranacademy.quran.ui.widgets.translationview

import android.content.Context
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.SpannedString
import android.util.AttributeSet
import android.widget.TextView
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import kotlinx.android.synthetic.main.dialog_footnote.view.*
import org.quranacademy.quran.di.getGlobal
import org.quranacademy.quran.domain.models.AyahTranslation
import org.quranacademy.quran.presentation.extensions.inflateThemed
import org.quranacademy.quran.presentation.ui.appearance.AppearanceManager
import org.quranacademy.quran.ui.htmlcompat.HtmlCompat
import org.quranacademy.quran.ui.translationtextview.R
import org.quranacademy.quran.ui.widgets.translationview.hqaformat.HQATranslationFormatTagHandler

class TranslationTextView @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyle: Int = 0
) : TextView(context, attrs, defStyle) {

    companion object {
        val appearanceManager by lazy { getGlobal<AppearanceManager>() }
    }

    init {
        movementMethod = ClickableMovementMethod.getInstance()
        isClickable = false
        isLongClickable = false
    }

    fun setTranslationText(ayahText: AyahTranslation, readMoreOption: ReadMoreOption) {
        val text = if (ayahText.textHQAFormat != null) {
            //кастомный конвертер из HTML в Spannable был использован по причине
            //невозможности получения атрибутов у обрабатываемых элементов
            val textHQAFormat = ayahText.textHQAFormat!!
            val tagHandler = HQATranslationFormatTagHandler(context)
            tagHandler.onFootnoteClick = { showFootnoteText(ayahText, it) }
            val formatterFootnoteText = HtmlCompat.fromHtml(context, textHQAFormat.text, 0, null, tagHandler)
            trimSpannable(formatterFootnoteText).apply {
                //багфикс: если футнот приходит в конце многострочного текста, например:
                // *************
                // ***#---------
                // где "*" - это текст, а "#" - футнот, то пустая зона после футнота (отмечена "-")
                // становится также кликабельной и при клике на неё открывается этот
                // самый футнот, а этого нам не надо
                append(SpannedString(" "))
            }
        } else {
            ayahText.simpleText!!.trim()
        }

        setText(text)
        //readMoreOption.addReadMoreTo(this, text)
        // ToDo: при включенной обрезе и медленном скролле снизу вверх, список подергивает.
        // Скорее всего, это происходит из за textView.post или подсчетов
        // Также есть проблема при переиспользовании TextView списком (RecyclerView) и
        // снизу остаются пустые строки
    }

    private fun trimSpannable(spannable: Spanned): SpannableStringBuilder {
        var trimStart = 0
        var trimEnd = 0

        var text = spannable.toString()

        while (text.isNotEmpty() && text.startsWith("\n")) {
            text = text.substring(1)
            trimStart += 1
        }

        while (text.isNotEmpty() && text.endsWith("\n")) {
            text = text.substring(0, text.length - 1)
            trimEnd += 1
        }

        return SpannableStringBuilder(spannable).delete(0, trimStart).delete(spannable.length - trimEnd, spannable.length)
    }

    private fun showFootnoteText(ayahText: AyahTranslation, footnoteId: String) {
        val currentTheme = appearanceManager.getCurrentAppThemeResId()
        val footnoteView = context.inflateThemed(R.layout.dialog_footnote, currentTheme)
        footnoteView.footnoteText.setHtmlText(ayahText.textHQAFormat!!.getFootnoteText(footnoteId))
        MaterialDialog(context).show {
            customView(view = footnoteView, scrollable = true)
            positiveButton(R.string.btn_label_ok)
        }
    }

}