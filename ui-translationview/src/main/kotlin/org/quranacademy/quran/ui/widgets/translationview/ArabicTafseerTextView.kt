package org.quranacademy.quran.ui.widgets.translationview

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.util.AttributeSet
import org.quranacademy.quran.domain.models.AyahTranslation
import org.quranacademy.quran.ui.translationtextview.R
import java.util.regex.Pattern

class ArabicTafseerTextView @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyle: Int = 0
) : AyahTranslationView(context, attrs, defStyle) {

    companion object {
        private var ARABIC_TEXT_SIZE_MULTIPLIER = 1.4f
        private var arabicTypeface: Typeface? = null
    }

    init {
        translationText.typeface = getArabicTypeface()
        translationText.layoutDirection = LAYOUT_DIRECTION_RTL
    }

    override fun setTranslationTypeface(font: Typeface?) {
        //ignore other typefaces
    }

    override fun setTextSize(textSize: Int) {
        translationText.textSize = textSize * ARABIC_TEXT_SIZE_MULTIPLIER
    }

    override fun setTranslation(ayahTranslation: AyahTranslation, readMoreOption: ReadMoreOption) {
        translationAuthorName.text = ayahTranslation.translation.name
        setHighlightedText(ayahTranslation, readMoreOption)
    }

    private fun setHighlightedText(ayahTranslation: AyahTranslation, readMoreOption: ReadMoreOption) {
        if (ayahTranslation.textHQAFormat != null) {
            translationText.setTranslationText(ayahTranslation, readMoreOption)
        } else {
            val arabicText = ayahTranslation.simpleText!!
                    .replace("{", "﴿")
                    .replace("}", "﴾")

            val pattern = Pattern
                    .compile("﴿(.*?)﴾", Pattern.CASE_INSENSITIVE)
            //build a spannable String using the Consequence
            val spannableString = SpannableString(arabicText)
            val matcher = pattern.matcher(arabicText)
            while (matcher.find()) {
                val start = matcher.start()
                val end = matcher.end()
                val coloredTextSpan = ForegroundColorSpan(Color.parseColor("#ff33b5e5"))
                spannableString.setSpan(coloredTextSpan, start, end, 0)
            }
            translationText.text = spannableString
        }

    }

    private fun getArabicTypeface(): Typeface {
        if (arabicTypeface == null) {
            arabicTypeface = Typeface.createFromAsset(context.assets, context.getString(R.string.font_kitab))
        }
        return arabicTypeface!!
    }

}