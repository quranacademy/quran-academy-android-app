package org.quranacademy.quran.ui.widgets.wordbywordview

import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import com.mta.tehreer.graphics.TypefaceManager
import com.mta.tehreer.widget.TLabel
import it.sephiroth.android.library.xtooltip.ClosePolicy
import it.sephiroth.android.library.xtooltip.Tooltip
import org.quranacademy.quran.domain.models.AyahWord
import org.quranacademy.quran.domain.models.AyahWordGroup
import org.quranacademy.quran.domain.models.AyahWordItem
import org.quranacademy.quran.presentation.extensions.getScreenSize
import org.quranacademy.quran.presentation.ui.global.ArabicTypeface
import org.quranacademy.quran.ui.wordbywordview.R

class WordByWordContainer @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyle: Int = 0
) : FlowLayout(context, attrs) {

    companion object {
        val DEFAULT_ARABIC_FONT = TypefaceManager.getTypeface(R.string.font_uthmanic_hafs_old)!!
    }

    private var arabicTypeface: ArabicTypeface = DEFAULT_ARABIC_FONT
    private var translationTypeface: Typeface? = null
    private var arabicTextSize = 16
    private var translationTextSize = 16
    private var isTajweedEnabled = false

    private val wordViews = mutableListOf<AyahWordView>()
    private val wordsGroupViews = mutableListOf<AyahWordsGroupView>()
    private val allWordViews = mutableListOf<View>()

    private val translationMaxWidth: Int by lazy {
        context.getScreenSize().x / 3
    }
    private var isTranslationTooltipsEnabled: Boolean = false

    fun setArabicTypeface(typeface: ArabicTypeface) {
        this.arabicTypeface = typeface

        wordViews.forEach {
            it.setArabicTypeface(typeface)
        }
        wordsGroupViews.forEach {
            it.setArabicTypeface(typeface)
        }
    }

    fun setTranslationTypeface(typeface: Typeface) {
        this.translationTypeface = typeface

        wordViews.forEach {
            it.setTranslationTypeface(typeface)
        }
        wordsGroupViews.forEach {
            it.setTranslationTypeface(typeface)
        }
    }

    fun setArabicTextSize(textSize: Int) {
        this.arabicTextSize = textSize

        wordViews.forEach {
            it.setArabicTextSize(textSize)
        }
        wordsGroupViews.forEach {
            it.setArabicTextSize(textSize)
        }
    }

    fun setTranslationTextSize(textSize: Int) {
        this.translationTextSize = textSize

        wordViews.forEach {
            it.setTranslationTextSize(textSize)
        }
        wordsGroupViews.forEach {
            it.setTranslationTextSize(textSize)
        }
    }

    fun setTranslationTooltipsEnabled(isEnabled: Boolean) {
        this.isTranslationTooltipsEnabled = isEnabled
    }

    fun setTextCenteringEnabled(isEnabled: Boolean) {
        if (isEnabled) {
            setGravity(Gravity.CENTER)
        } else {
            setGravity(Gravity.RIGHT)
        }
    }

    fun setTajweedEnabled(isEnabled: Boolean) {
        this.isTajweedEnabled = isEnabled

        wordViews.forEach {
            it.setTajweedEnabled(isEnabled)
        }
        wordsGroupViews.forEach {
            it.setTajweedEnabled(isEnabled)
        }
    }

    fun highlightWordWithPosition(nowPlayingWordNumber: Int?) {
        allWordViews.forEach { it.background = null }
        nowPlayingWordNumber?.let {
            //минусуем 1 т. к. нумерация начинается с 1, а отсчет позиции с нуля
            val view = allWordViews[it - 1]
            if (view is AyahWordView) {
                view.setArabicTextBackground(R.drawable.highlighted_word_bg)
            } else if (view is TLabel) {
                view.setBackgroundResource(R.drawable.highlighted_word_bg)
            }
        }
    }

    fun setWords(words: List<AyahWordItem>) {
        wordViews.clear()
        wordsGroupViews.clear()
        allWordViews.clear()
        removeAllViews()
        words.forEach { word ->
            if (word is AyahWord) {
                val wordView = AyahWordView(context)
                wordView.setArabicTypeface(arabicTypeface)
                translationTypeface?.let { wordView.setTranslationTypeface(it) }
                wordView.setArabicTextSize(arabicTextSize)
                wordView.setTranslationTextSize(translationTextSize)
                wordView.setTajweedEnabled(isTajweedEnabled)
                wordView.setTranslationMaxWidth(translationMaxWidth)
                wordView.setData(word)
                addView(wordView)
                wordViews.add(wordView)
                allWordViews.add(wordView)

                if (isTranslationTooltipsEnabled && word.translationText != null) {
                    wordView.hideTranslationText()
                    setupViewTooltip(
                            view = wordView,
                            text = word.translationText!!,
                            highlightText = { wordView.highlightArabicText(it) }
                    )
                }
            } else if (word is AyahWordGroup) {
                val wordsGroupView = AyahWordsGroupView(context)
                wordsGroupView.setArabicTypeface(arabicTypeface)
                translationTypeface?.let { wordsGroupView.setTranslationTypeface(it) }
                wordsGroupView.setArabicTextSize(arabicTextSize)
                wordsGroupView.setTranslationTextSize(translationTextSize)
                wordsGroupView.setTajweedEnabled(isTajweedEnabled)
                wordsGroupView.setTranslationMaxWidth(translationMaxWidth)
                wordsGroupView.setData(word)
                addView(wordsGroupView)
                wordsGroupViews.add(wordsGroupView)
                allWordViews.addAll(wordsGroupView.wordViews)

                if (isTranslationTooltipsEnabled) {
                    wordsGroupView.hideTranslationText()
                    setupViewTooltip(
                            view = wordsGroupView,
                            text = word.translationText,
                            highlightText = { wordsGroupView.highlightArabicText(it) }
                    )
                }
            }
        }
    }

    private fun setupViewTooltip(
            view: View,
            text: String,
            highlightText: (Boolean) -> Unit
    ) {
        val tooltip = Tooltip.Builder(context)
                .anchor(view, 0, 0, false)
                .text(text)
                .styleId(R.style.AyahTooltipStyle)
                .arrow(true)
                .closePolicy(ClosePolicy.TOUCH_ANYWHERE_CONSUME)
                .overlay(false)
                .create()
                .doOnShown { highlightText(true) }
                .doOnHidden { highlightText(false) }

        view.setOnClickListener {
            tooltip.show(view, Tooltip.Gravity.TOP, true)
        }
    }

    fun clear() = removeAllViews()

}