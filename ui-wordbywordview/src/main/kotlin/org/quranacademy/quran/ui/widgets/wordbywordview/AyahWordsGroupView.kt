package org.quranacademy.quran.ui.widgets.wordbywordview

import android.content.Context
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.graphics.Typeface
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.RectShape
import android.text.Html
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import com.mta.tehreer.widget.TLabel
import org.quranacademy.quran.domain.models.AyahWordGroup
import org.quranacademy.quran.presentation.extensions.dp
import org.quranacademy.quran.presentation.extensions.getThemeColor
import org.quranacademy.quran.presentation.extensions.visible
import org.quranacademy.quran.presentation.ui.global.ArabicTypeface
import org.quranacademy.quran.ui.wordbywordview.R

class AyahWordsGroupView @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyle: Int = 0
) : LinearLayout(context, attrs, defStyle) {

    private lateinit var arabicTypeface: ArabicTypeface
    private val wordsContainer = LinearLayout(context)
    private val translationText = TightTextView(context)
    private val primaryTextColor = context.getThemeColor(android.R.attr.textColorPrimary)

    private var arabicTextSizePx: Float = 16f
    private var isTajweedEnabled = false

    private lateinit var wordGroup: AyahWordGroup
    internal val wordViews = mutableListOf<TLabel>()

    init {
        orientation = VERTICAL
        gravity = Gravity.CENTER
        layoutDirection = View.LAYOUT_DIRECTION_RTL

        wordsContainer.setBackgroundResource(R.drawable.words_group_bg)
        wordsContainer.orientation = HORIZONTAL
        val wordsContainerLp = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
        addView(wordsContainer, wordsContainerLp)
        setDividerWidth(5)

        translationText.setTextColor(primaryTextColor)
        translationText.gravity = Gravity.CENTER

        val translationTextLp = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
        addView(translationText, translationTextLp)
    }

    fun setArabicTypeface(typeface: ArabicTypeface) {
        this.arabicTypeface = typeface
        wordViews.forEach { it.typeface = typeface }
    }

    fun setTranslationTypeface(typeface: Typeface) {
        translationText.typeface = typeface
    }

    fun setArabicTextSize(textSize: Int) {
        arabicTextSizePx = textSize.dp.toFloat()
        wordViews.forEach { it.textSize = arabicTextSizePx }
    }

    fun highlightArabicText(isHighlighted: Boolean) {
        wordViews.forEach {
            it.textColor = if (isHighlighted) {
                context.getThemeColor(R.attr.colorAccent)
            } else {
                context.getThemeColor(android.R.attr.textColorPrimary)
            }
        }
    }

    fun hideTranslationText() {
        translationText.visible(false)
        wordsContainer.setBackgroundColor(Color.TRANSPARENT)
        setDividerWidth(0)
    }

    fun setTranslationTextSize(textSize: Int) {
        translationText.textSize = textSize.toFloat()
    }

    fun setTranslationMaxWidth(maxWidth: Int) {
        translationText.maxWidth = maxWidth
    }

    fun setTajweedEnabled(isEnabled: Boolean) {
        isTajweedEnabled = isEnabled
        updateArabicText()
    }

    fun setData(wordGroup: AyahWordGroup) {
        this.wordGroup = wordGroup
        translationText.text = wordGroup.translationText

        wordViews.clear()
        //reverse items to implement RTL mode
        wordGroup.words.forEach { word ->
            val arabicTextLabel = TLabel(context)
            arabicTextLabel.textColor = primaryTextColor
            arabicTextLabel.textSize = arabicTextSizePx

            if (::arabicTypeface.isInitialized) {
                arabicTextLabel.typeface = arabicTypeface
            }

            //Bug-fix: Add horizontal and vertical padding to avoid cutting text
            val horizontalPadding = 5.dp
            val verticalPadding = 3.dp
            arabicTextLabel.setPadding(horizontalPadding, verticalPadding, horizontalPadding, verticalPadding)

            if (word.isSajdaWord) {
                val sajdaBg = resources.getDrawable(R.drawable.sajda_word_bg)
                val sajdaLineColor = context.getThemeColor(android.R.attr.textColorPrimary)
                sajdaBg.colorFilter = PorterDuffColorFilter(sajdaLineColor, PorterDuff.Mode.SRC_ATOP)
                arabicTextLabel.background = sajdaBg
            }

            val arabicTextLp = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
            wordsContainer.addView(arabicTextLabel, arabicTextLp)
            wordViews.add(arabicTextLabel)
        }
        updateArabicText()
    }

    private fun setDividerWidth(size: Int) {
        val drawable = ShapeDrawable(RectShape())
        drawable.alpha = 0
        drawable.intrinsicWidth = size.dp
        wordsContainer.dividerDrawable = drawable
        wordsContainer.showDividers = SHOW_DIVIDER_BEGINNING
    }

    private fun updateArabicText() {
        if (::wordGroup.isInitialized) {
            val words = wordGroup.words
            if (isTajweedEnabled) {
                wordViews.forEachIndexed { index, label ->
                    label.spanned = Html.fromHtml(words[index].arabicTextTajweed)
                }
            } else {
                wordViews.forEachIndexed { index, label ->
                    label.text = words[index].arabicText
                }
            }
        }
    }

}