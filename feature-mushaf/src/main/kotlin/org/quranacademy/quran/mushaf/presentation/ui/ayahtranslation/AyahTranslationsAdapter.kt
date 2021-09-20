package org.quranacademy.quran.mushaf.presentation.ui.ayahtranslation

import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.view.ContextThemeWrapper
import androidx.viewpager.widget.PagerAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.quranacademy.quran.domain.models.AyahDetails
import org.quranacademy.quran.mushaf.R
import org.quranacademy.quran.presentation.ui.appearance.AppearanceManager
import org.quranacademy.quran.presentation.ui.global.getTypeface

class AyahTranslationsAdapter(
        private val context: Context,
        private val appearanceManager: AppearanceManager,
        coroutineScope: CoroutineScope
) : PagerAdapter() {

    private val ayahDetails = mutableListOf<AyahDetails>()
    private val itemViews = mutableMapOf<Int, AyahDetailsLayout>()

    init {
        coroutineScope.launch {
            appearanceManager.getArabicTextSizeUpdates()
                    .collect { textSize ->
                        itemViews.values.forEach {
                            it.setArabicTextSize(textSize)
                        }
                    }
        }

        coroutineScope.launch {
            appearanceManager.getTranslationTextSizeUpdates()
                    .collect { textSize ->
                        itemViews.values.forEach {
                            it.setTranslationTextSize(textSize)
                        }
                    }
        }

        coroutineScope.launch {
            appearanceManager.getWbwTranslationTextSizeUpdates()
                    .collect { textSize ->
                        itemViews.values.forEach {
                            it.setWbwTranslationTextSize(textSize)
                        }
                    }
        }

        coroutineScope.launch {
            appearanceManager.getArabicFontUpdates()
                    .collect { arabicFont ->
                        val typeface = arabicFont.getTypeface(context)
                        itemViews.values.forEach {
                            it.setArabicTextTypeface(typeface)
                        }
                    }
        }

        coroutineScope.launch {
            appearanceManager.getTranslationFontUpdates()
                    .collect { translationFont ->
                        val typeface = translationFont.getTypeface(context)
                        itemViews.values.forEach {
                            it.setTranslationTypeface(typeface)
                        }
                    }
        }

        coroutineScope.launch {
            appearanceManager.getArabicTextCenteringEnabledUpdates()
                    .collect { isCenteringEnabled ->
                        itemViews.values.forEach {
                            it.setArabicTextCenteringEnabled(isCenteringEnabled)
                        }
                    }
        }

        coroutineScope.launch {
            appearanceManager.getTranslationTextCenteringEnabledUpdates()
                    .collect { isCenteringEnabled ->
                        itemViews.values.forEach {
                            it.setTranslationTextCenteringEnabled(isCenteringEnabled)
                        }
                    }
        }

        coroutineScope.launch {
            appearanceManager.isTajweedEnabledUpdates()
                    .collect { isTajweedEnabled ->
                        itemViews.values.forEach {
                            it.setTajweedEnabled(isTajweedEnabled)
                        }
                    }
        }
    }

    override fun getItemPosition(`object`: Any): Int = POSITION_NONE

    override fun getCount(): Int = ayahDetails.size

    override fun instantiateItem(parent: ViewGroup, position: Int): Any {
        val ayahTranslation = ayahDetails[position]

        val ayahTranslationsLayout = AyahDetailsLayout(ContextThemeWrapper(context, R.style.TranslationsPanel))
        ayahTranslationsLayout.setArabicTextTypeface(appearanceManager.getArabicFont().getTypeface(context))
        ayahTranslationsLayout.setArabicTextSize(appearanceManager.getArabicTextSize())
        ayahTranslationsLayout.setArabicTextCenteringEnabled(appearanceManager.isQuranTextCenteringEnabled())
        ayahTranslationsLayout.setTranslationTypeface(appearanceManager.getTranslationFont().getTypeface(context))
        ayahTranslationsLayout.setTranslationTextSize(appearanceManager.getTranslationTextSize())
        ayahTranslationsLayout.setWbwTranslationTextSize(appearanceManager.getWbwTranslationTextSize())
        ayahTranslationsLayout.setTranslationTextCenteringEnabled(appearanceManager.isTranslationTextCenteringEnabled())
        ayahTranslationsLayout.setTajweedEnabled(appearanceManager.isTajweedEnabled())
        ayahTranslationsLayout.setAyahDetails(ayahTranslation)
        ayahTranslationsLayout.setBackgroundColor(context.resources.getColor(R.color.window_background_night))
        parent.addView(ayahTranslationsLayout)

        itemViews[position] = ayahTranslationsLayout

        return ayahTranslationsLayout
    }

    override fun destroyItem(collection: ViewGroup, position: Int, view: Any) {
        itemViews.remove(position)
        collection.removeView(view as View)
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view === `object`
    }

    fun setData(translations: List<AyahDetails>) {
        ayahDetails.clear()
        ayahDetails.addAll(translations)
        notifyDataSetChanged()
    }

}