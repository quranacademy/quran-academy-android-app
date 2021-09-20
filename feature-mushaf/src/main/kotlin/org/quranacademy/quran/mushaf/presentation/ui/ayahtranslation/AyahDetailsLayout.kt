package org.quranacademy.quran.mushaf.presentation.ui.ayahtranslation

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.widget.NestedScrollView
import org.quranacademy.quran.domain.models.AyahDetails
import org.quranacademy.quran.presentation.extensions.dp
import org.quranacademy.quran.presentation.extensions.getThemeColor
import org.quranacademy.quran.presentation.extensions.visible
import org.quranacademy.quran.presentation.ui.global.ArabicTypeface
import org.quranacademy.quran.ui.widgets.translationview.AyahTranslationsContainer
import org.quranacademy.quran.ui.widgets.wordbywordview.WordByWordContainer

class AyahDetailsLayout @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyle: Int = 0
) : LinearLayout(context, attrs, defStyle) {

    private val wordByWordContainer = WordByWordContainer(context)
    private val translationsContainer = AyahTranslationsContainer(context)
    private val title = TextView(context)

    init {
        orientation = VERTICAL

        val translationsContainerScroll = NestedScrollView(context)
        addView(translationsContainerScroll)

        translationsContainer.orientation = VERTICAL
        translationsContainer.setPadding(16.dp, 16.dp, 16.dp, 46.dp)
        val translationsContainerLp = LayoutParams(MATCH_PARENT, WRAP_CONTENT)
        translationsContainerScroll.addView(translationsContainer, translationsContainerLp)

        title.setTextColor(context.getThemeColor(android.R.attr.textColorSecondary))
        val titleLp = LayoutParams(MATCH_PARENT, WRAP_CONTENT)
        titleLp.bottomMargin = 16.dp
        translationsContainer.addView(title, titleLp)

        wordByWordContainer.visible(false)
        wordByWordContainer.rowSpacing = 5.dp.toFloat()
        wordByWordContainer.childSpacing = 5.dp
        val wordByWordContainerLp = LayoutParams(MATCH_PARENT, WRAP_CONTENT)
        wordByWordContainerLp.bottomMargin = 16.dp
        translationsContainer.addView(wordByWordContainer, wordByWordContainerLp)
    }

    fun setArabicTextTypeface(typeface: ArabicTypeface) {
        wordByWordContainer.setArabicTypeface(typeface)
    }

    fun setArabicTextSize(textSize: Int) {
        wordByWordContainer.setArabicTextSize(textSize)
    }

    fun setArabicTextCenteringEnabled(isEnabled: Boolean) {
        wordByWordContainer.setTextCenteringEnabled(isEnabled)
    }

    fun setTranslationTypeface(typeface: Typeface) {
        translationsContainer.setTranslationTypeface(typeface)
        wordByWordContainer.setTranslationTypeface(typeface)
    }

    fun setTranslationTextSize(textSize: Int) {
        translationsContainer.setTranslationTextSize(textSize)
    }

    fun setWbwTranslationTextSize(textSize: Int) {
        wordByWordContainer.setTranslationTextSize(textSize)
    }

    fun setTranslationTextCenteringEnabled(isEnabled: Boolean) {
        translationsContainer.setTextCenteringEnabled(isEnabled)
    }

    fun setTajweedEnabled(isEnabled: Boolean) {
        wordByWordContainer.setTajweedEnabled(isEnabled)
    }

    @SuppressLint("SetTextI18n")
    fun setAyahDetails(ayahDetails: AyahDetails) {
        title.text = "${ayahDetails.surahNumber}:${ayahDetails.ayahNumber}"

        val ayahWords = ayahDetails.words
        if (ayahWords != null) {
            wordByWordContainer.visible(true)
            wordByWordContainer.setWords(ayahWords)
        }

        translationsContainer.setAyahTranslations(ayahDetails.translations)
    }

}