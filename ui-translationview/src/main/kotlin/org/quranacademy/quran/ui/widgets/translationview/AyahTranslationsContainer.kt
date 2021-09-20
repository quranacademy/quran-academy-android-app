package org.quranacademy.quran.ui.widgets.translationview

import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet
import android.widget.LinearLayout
import org.quranacademy.quran.domain.models.AyahTranslation
import org.quranacademy.quran.ui.translationtextview.R

class AyahTranslationsContainer @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyle: Int = 0
) : LinearLayout(context, attrs, defStyle) {

    private val ayahTranslationViews = mutableListOf<AyahTranslationView>()
    private var translationTypeface: Typeface? = null
    private var translationTextSize: Int = 14
    private var isTextCenteringEnabled: Boolean = false

    private val readMoreOption = ReadMoreOption.Builder(context)
            .textLength(10, ReadMoreOption.TYPE_LINE)
            .moreLabel(resources.getString(R.string.read_more_label))
            .lessLabel(resources.getString(R.string.read_less_label))
            .moreLabelColor(resources.getColor(R.color.accent))
            .lessLabelColor(resources.getColor(R.color.accent))
            .showLessLabel(false)
            .build()

    fun setTranslationTypeface(typeface: Typeface) {
        translationTypeface = typeface
        ayahTranslationViews.forEach { it.setTranslationTypeface(typeface) }
    }

    fun setTranslationTextSize(textSize: Int) {
        translationTextSize = textSize
        ayahTranslationViews.forEach { it.setTextSize(textSize) }
    }

    fun setTextCenteringEnabled(isTextCenteringEnabled: Boolean) {
        this.isTextCenteringEnabled = isTextCenteringEnabled
        ayahTranslationViews.forEach { it.setTextCenteringEnabled(isTextCenteringEnabled) }
    }

    fun setAyahTranslations(translations: List<AyahTranslation>) {
        ayahTranslationViews.clear()
        translations.forEach { ayahTranslation ->
            createAyahTranslationView(ayahTranslation)
        }
    }

    private fun createAyahTranslationView(ayahTranslation: AyahTranslation) {
        val ayahTranslationView = if (!ayahTranslation.translation.isArabic) {
            AyahTranslationView(context)
        } else {
            //отдельная вьюшка для арабского
            ArabicTafseerTextView(context)
        }

        ayahTranslationView.setTranslationTypeface(translationTypeface)
        ayahTranslationView.setTextSize(translationTextSize)
        ayahTranslationView.setTextCenteringEnabled(isTextCenteringEnabled)
        ayahTranslationView.setTranslation(ayahTranslation, readMoreOption)
        addView(ayahTranslationView)
        ayahTranslationViews.add(ayahTranslationView)

    }


}