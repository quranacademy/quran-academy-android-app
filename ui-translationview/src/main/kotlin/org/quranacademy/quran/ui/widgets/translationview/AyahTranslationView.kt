package org.quranacademy.quran.ui.widgets.translationview

import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.TextView
import org.quranacademy.quran.domain.models.AyahTranslation
import org.quranacademy.quran.presentation.extensions.dp
import org.quranacademy.quran.presentation.extensions.getThemeColor

open class AyahTranslationView @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyle: Int = 0
) : LinearLayout(context, attrs, defStyle) {

    protected val translationAuthorName = TextView(context)
    protected val translationText = TranslationTextView(context)

    init {
        orientation = VERTICAL

        val textColorSecondary = context.getThemeColor(android.R.attr.textColorSecondary)

        translationAuthorName.setTextColor(textColorSecondary)
        val authorNameLp = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
        authorNameLp.topMargin = 8.dp
        addView(translationAuthorName, authorNameLp)

        val textColorPrimary = context.getThemeColor(android.R.attr.textColorPrimary)
        translationText.setTextColor(textColorPrimary)
        val translationTextLp = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
        translationTextLp.topMargin = 8.dp
        addView(translationText, translationTextLp)
    }

    open fun setTranslationTypeface(font: Typeface?) {
        if (font != null) {
            translationText.typeface = font
        }
    }

    open fun setTextSize(textSize: Int) {
        translationText.textSize = textSize.toFloat()
    }

    fun setTextCenteringEnabled(textCenteringEnabled: Boolean) {
        translationText.gravity = if (textCenteringEnabled) Gravity.CENTER else Gravity.START
    }

    open fun setTranslation(ayahTranslation: AyahTranslation, readMoreOption: ReadMoreOption) {
        translationAuthorName.text = ayahTranslation.translation.name
        translationText.setTranslationText(ayahTranslation, readMoreOption)
    }

}