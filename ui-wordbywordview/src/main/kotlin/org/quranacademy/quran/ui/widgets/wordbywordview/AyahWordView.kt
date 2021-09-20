package org.quranacademy.quran.ui.widgets.wordbywordview

import android.content.Context
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.graphics.Typeface
import android.util.AttributeSet
import android.view.Gravity
import android.widget.LinearLayout
import com.mta.tehreer.widget.TLabel
import org.quranacademy.quran.core.ui.R
import org.quranacademy.quran.domain.models.AyahWord
import org.quranacademy.quran.presentation.extensions.*
import org.quranacademy.quran.presentation.ui.global.ArabicTypeface

class AyahWordView @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyle: Int = 0
) : LinearLayout(context, attrs, defStyle) {

    private val arabicText = TLabel(context)
    private val translationText = TightTextView(context)
    private var isTajweedEnabled = false

    private lateinit var word: AyahWord

    init {
        orientation = VERTICAL
        gravity = Gravity.CENTER

        val primaryTextColor = context.getThemeColor(android.R.attr.textColorPrimary)

        arabicText.textColor = primaryTextColor
        //Bug-fix: Add horizontal padding to avoid cutting "madda" in of the text
        val horizontalPadding = 5.dp
        val verticalPadding = 3.dp
        arabicText.setPadding(horizontalPadding, verticalPadding, horizontalPadding, verticalPadding)

        val arabicTextLp = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
        addView(arabicText, arabicTextLp)

        translationText.setTextColor(primaryTextColor)
        translationText.gravity = Gravity.CENTER

        val translationTextLp = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
        addView(translationText, translationTextLp)
    }

    fun setArabicTypeface(typeface: ArabicTypeface) {
        arabicText.typeface = typeface
    }

    fun highlightArabicText(isHighlighted: Boolean) {
        arabicText.textColor = if (isHighlighted) {
            context.getThemeColor(R.attr.colorAccent)
        } else {
            context.getThemeColor(android.R.attr.textColorPrimary)
        }
    }

    fun setTranslationTypeface(typeface: Typeface) {
        translationText.typeface = typeface
    }

    fun setTranslationMaxWidth(maxWidth: Int) {
        translationText.maxWidth = maxWidth
    }

    fun hideTranslationText() {
        translationText.visible(false)
    }

    fun setArabicTextBackground(backgroundResId: Int) {
        arabicText.setBackgroundResource(backgroundResId)
    }

    fun setData(word: AyahWord) {
        this.word = word
        updateArabicText()
        translationText.visible(word.translationText != null)
        translationText.text = word.translationText

        if (word.isSajdaWord) {
            val sajdaBg = resources.getDrawable(R.drawable.sajda_word_bg)
            val sajdaLineColor = context.getThemeColor(android.R.attr.textColorPrimary)
            sajdaBg.colorFilter = PorterDuffColorFilter(sajdaLineColor, PorterDuff.Mode.SRC_ATOP)
            background = sajdaBg
        }
    }

    fun setArabicTextSize(textSize: Int) {
        arabicText.textSize = textSize.dp.toFloat()
    }

    fun setTranslationTextSize(textSize: Int) {
        translationText.textSize = textSize.toFloat()
    }

    fun setTajweedEnabled(isEnabled: Boolean) {
        isTajweedEnabled = isEnabled
        updateArabicText()
    }

    private fun updateArabicText() {
        if (::word.isInitialized) {
            if (isTajweedEnabled) {
                arabicText.spanned = word.arabicTextTajweed.fromHtml()
            } else {
                arabicText.text = word.arabicText
            }
        }
    }

}