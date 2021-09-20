package org.quranacademy.quran.surahdetails.ui

import android.content.Context
import android.graphics.Typeface
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hannesdorfmann.adapterdelegates3.AdapterDelegate
import kotlinx.android.synthetic.main.item_ayah.view.*
import kotlinx.android.synthetic.main.item_ayah_page_number_divider.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.quranacademy.quran.data.prefs.AppPreferences
import org.quranacademy.quran.domain.models.ArabicFont
import org.quranacademy.quran.extensions.mergeWith
import org.quranacademy.quran.presentation.extensions.applyTypeface
import org.quranacademy.quran.presentation.extensions.getThemeColor
import org.quranacademy.quran.presentation.extensions.inflate
import org.quranacademy.quran.presentation.extensions.visible
import org.quranacademy.quran.presentation.ui.appearance.AppearanceManager
import org.quranacademy.quran.presentation.ui.global.*
import org.quranacademy.quran.surahdetails.R
import org.quranacademy.quran.surahdetails.mvp.AyahUiModel
import org.quranacademy.quran.ui.widgets.wordbywordview.WordByWordContainer

class AyahAdapterDelegate(
        private val context: Context,
        private val onClickListener: (AyahUiModel) -> Unit,
        private val appearanceManager: AppearanceManager,
        private val appPreferences: AppPreferences,
        private val coroutineScope: CoroutineScope
) : AdapterDelegate<MutableList<Any>>() {

    private val isCurrentArabicLanguage = getCurrentAppLanguage().isArabic()
    private var quranTypeface: ArabicTypeface = appearanceManager.getArabicFont().getTypeface(context)
    private var translationTypeface: Typeface = appearanceManager.getTranslationFont().getTypeface(context)
    private var arabicTextSize = appearanceManager.getArabicTextSize()
    private var translationTextSize = appearanceManager.getTranslationTextSize()
    private var wbwTranslationTextSize = appearanceManager.getWbwTranslationTextSize()
    private var isQuranTextCenteringEnabled = appearanceManager.isQuranTextCenteringEnabled()
    private var isTranslationCenterTextEnabled = appearanceManager.isTranslationTextCenteringEnabled()
    private var isTajweedEnabled = appearanceManager.isTajweedEnabled()
    internal var nowPlayingAyahNumber: Int? = null
    private var nowPlayingWordNumber: Int? = null

    override fun isForViewType(items: MutableList<Any>, position: Int) =
            items[position] is AyahUiModel

    override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        val context = parent.context
        val itemView = parent.inflate(R.layout.item_ayah)

        if (isCurrentArabicLanguage) {
            itemView.ayahNumber.applyTypeface(R.string.font_kitab)
        }

        itemView.wordsWithTranslationContainer.setArabicTypeface(quranTypeface)
        itemView.wordsWithTranslationContainer.setArabicTextSize(arabicTextSize)
        itemView.wordsWithTranslationContainer.setTranslationTypeface(translationTypeface)
        itemView.wordsWithTranslationContainer.setTranslationTextSize(wbwTranslationTextSize)
        itemView.wordsWithTranslationContainer.setTajweedEnabled(isTajweedEnabled)
        itemView.wordsWithTranslationContainer.setTextCenteringEnabled(isQuranTextCenteringEnabled)

        itemView.wordsContainer.setTranslationTooltipsEnabled(true)
        itemView.wordsContainer.setArabicTypeface(quranTypeface)
        itemView.wordsContainer.setArabicTextSize(arabicTextSize)
        itemView.wordsContainer.setTranslationTypeface(translationTypeface)
        itemView.wordsContainer.setTranslationTextSize(wbwTranslationTextSize)
        itemView.wordsContainer.setTajweedEnabled(isTajweedEnabled)
        itemView.wordsContainer.setTextCenteringEnabled(isQuranTextCenteringEnabled)

        itemView.translationsContainer.setTranslationTypeface(translationTypeface)
        itemView.translationsContainer.setTranslationTextSize(translationTextSize)
        itemView.translationsContainer.setTextCenteringEnabled(isTranslationCenterTextEnabled)

        val viewHolder = ViewHolder(itemView)

        fun updateQuranFont(font: ArabicFont) {
            quranTypeface = font.getTypeface(context)
            itemView.wordsWithTranslationContainer.setArabicTypeface(quranTypeface)
            itemView.wordsContainer.setArabicTypeface(quranTypeface)
        }

        coroutineScope.launch {
            appearanceManager.getArabicFontUpdates()
                    //т. к. выбор шрифта тексты таджвида и пословного не обновлены, нам
                    //нам нужно следить за их переключением и, соответственно, менять шрифт
                    //все эти костыли можно удалить после обновления текста пословного и таджвида
                    //При добавлении новых шрифтов необходимо оптимизировать их под новый текст
                    .mergeWith(appPreferences.getWbwEnablingUpdates())
                    .mergeWith(appearanceManager.isTajweedEnabledUpdates())
                    .collect { updateQuranFont(appearanceManager.getArabicFont()) }
        }

        coroutineScope.launch {
            appearanceManager.getTranslationFontUpdates()
                    .collect {
                        translationTypeface = it.getTypeface(context)
                        itemView.translationsContainer.setTranslationTypeface(translationTypeface)
                        itemView.wordsContainer.setTranslationTypeface(translationTypeface)
                    }
        }

        coroutineScope.launch {
            appearanceManager.getArabicTextSizeUpdates()
                    .collect {
                        arabicTextSize = it
                        itemView.wordsWithTranslationContainer.setArabicTextSize(it)
                        itemView.wordsContainer.setArabicTextSize(it)
                    }
        }


        coroutineScope.launch {
            appearanceManager.getWbwTranslationTextSizeUpdates()
                    .collect {
                        wbwTranslationTextSize = it
                        itemView.wordsWithTranslationContainer.setTranslationTextSize(it)
                        itemView.wordsContainer.setTranslationTextSize(it)
                    }
        }

        coroutineScope.launch {
            appearanceManager.getTranslationTextSizeUpdates()
                    .collect {
                        translationTextSize = it
                        itemView.translationsContainer.setTranslationTextSize(it)
                    }
        }

        coroutineScope.launch {
            appearanceManager.getArabicTextCenteringEnabledUpdates()
                    .collect {
                        isQuranTextCenteringEnabled = it
                        itemView.wordsWithTranslationContainer.setTextCenteringEnabled(it)
                        itemView.wordsContainer.setTextCenteringEnabled(it)
                    }
        }

        coroutineScope.launch {
            appearanceManager.getTranslationTextCenteringEnabledUpdates()
                    .collect {
                        isTranslationCenterTextEnabled = it
                        itemView.translationsContainer.setTextCenteringEnabled(it)
                    }
        }

        coroutineScope.launch {
            appearanceManager.isTajweedEnabledUpdates()
                    .collect { isTajweedEnabled ->
                        this@AyahAdapterDelegate.isTajweedEnabled = isTajweedEnabled
                        itemView.wordsContainer.setTajweedEnabled(isTajweedEnabled)
                    }
        }

        return viewHolder
    }

    override fun onBindViewHolder(
            items: MutableList<Any>,
            position: Int,
            viewHolder: RecyclerView.ViewHolder,
            payloads: MutableList<Any>
    ) = (viewHolder as ViewHolder).bind(items[position] as AyahUiModel, position)

    fun highlightNowPlayingAyah(ayahNumber: Int?) {
        this.nowPlayingAyahNumber = ayahNumber
    }

    fun highlightNowPlayingWord(wordNumber: Int?) {
        this.nowPlayingWordNumber = wordNumber
    }

    private inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        private lateinit var ayah: AyahUiModel

        init {
            view.setOnClickListener { onClickListener(ayah) }
        }

        fun bind(ayah: AyahUiModel, position: Int) {
            this.ayah = ayah

            if (ayah.showPageAyah) {
                itemView.ayahPageNumberContainer.visible(true)
                itemView.ayahPageNumber.text = context.getString(
                        R.string.page_number_template,
                        ayah.pageNumber.toArabicNumberIfNeeded(isCurrentArabicLanguage)
                )
            } else {
                itemView.ayahPageNumberContainer.visible(false)
            }

            itemView.ayahNumber.text = ayah.ayahNumber.toArabicNumberIfNeeded(isCurrentArabicLanguage)
            itemView.sajdaSymbol.visible(ayah.isSajdaAyah)

            val isAyahPlaying = ayah.ayahNumber == nowPlayingAyahNumber
            if (isAyahPlaying) {
                itemView.setBackgroundColor(context.getThemeColor(R.attr.ayahAudioHighlightColor))
            } else {
                val resId = if (position % 2 == 0) R.attr.windowBackgroundSecondary else android.R.attr.windowBackground
                itemView.setBackgroundColor(context.getThemeColor(resId))
            }

            itemView.wordsContainer.clear()

            val isWordByWordTranslationEnabled = ayah.isWordByWordEnabled
            itemView.wordsWithTranslationContainer.visible(isWordByWordTranslationEnabled)
            itemView.wordsContainer.visible(!isWordByWordTranslationEnabled)

            if (isWordByWordTranslationEnabled) {
                itemView.wordsWithTranslationContainer.setWords(ayah.words)
            } else {
                itemView.wordsContainer.setWords(ayah.words)
            }

            if (isAyahPlaying) {
                val container: WordByWordContainer = if (isWordByWordTranslationEnabled) {
                    itemView.wordsWithTranslationContainer
                } else itemView.wordsContainer
                container.highlightWordWithPosition(nowPlayingWordNumber)
            }

            itemView.translationsContainer.removeAllViews()
            itemView.translationsContainer.setAyahTranslations(ayah.translations)
        }

    }

}