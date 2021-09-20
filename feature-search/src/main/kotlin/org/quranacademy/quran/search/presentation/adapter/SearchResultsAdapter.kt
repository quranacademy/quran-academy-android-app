package org.quranacademy.quran.search.presentation.adapter

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_search_result.view.*
import org.quranacademy.quran.Constants
import org.quranacademy.quran.domain.models.AppTheme
import org.quranacademy.quran.presentation.extensions.fromHtml
import org.quranacademy.quran.presentation.extensions.getThemeColor
import org.quranacademy.quran.presentation.extensions.inflate
import org.quranacademy.quran.presentation.extensions.visible
import org.quranacademy.quran.presentation.ui.appearance.AppearanceManager
import org.quranacademy.quran.presentation.ui.appearance.LanguageManager
import org.quranacademy.quran.presentation.ui.global.getCurrentAppLanguage
import org.quranacademy.quran.presentation.ui.global.isArabic
import org.quranacademy.quran.presentation.ui.global.toArabicNumberIfNeeded
import org.quranacademy.quran.search.R
import org.quranacademy.quran.search.domain.QuranSearchResults
import org.quranacademy.quran.search.domain.SearchResult
import java.util.regex.Pattern

class SearchResultsAdapter(
        private val appearanceManager: AppearanceManager,
        private val languageManager: LanguageManager,
        private val onClickListener: (SearchResult) -> Unit
) : RecyclerView.Adapter<SearchResultsAdapter.Holder>() {

    private lateinit var results: QuranSearchResults
    private val isCurrentArabicLanguage = getCurrentAppLanguage().isArabic()
    private lateinit var arabicHighlightRegex: Pattern

    override fun getItemCount() = if (::results.isInitialized) results.results.size else 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view = parent.inflate(R.layout.item_search_result)
        return Holder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: Holder, position: Int) {
        val itemView = holder.itemView
        val searchResult = results.results[position]

        itemView.arabicText.visible(results.isArabic)
        itemView.translationText.visible(!results.isArabic)
        if (results.isArabic) {
            itemView.arabicText.spanned = ArabicTextHighlighter.highlightArabic(
                    text = searchResult.text,
                    regex = arabicHighlightRegex,
                    color = itemView.context.getThemeColor(R.attr.searchTextHighlightColor)
            )
        } else {
            itemView.translationText.text = fixRtlInText(searchResult.text)
                    .replace("<highlight>", "<font color=\"${getHighlightColor()}\">")
                    .replace("</highlight>", "</font>")
                    .fromHtml()
        }

        val positionText = (position + 1).toArabicNumberIfNeeded(isCurrentArabicLanguage)
        itemView.searchResultDetails.text = "$positionText - ${searchResult.description}"

        itemView.setOnClickListener { onClickListener(searchResult) }
    }

    fun setData(searchResults: QuranSearchResults) {
        this.arabicHighlightRegex = ArabicTextHighlighter.createArabicRegex(searchResults.query)
        this.results = searchResults
        notifyDataSetChanged()
    }

    private fun fixRtlInText(text: String): String {
        val isCurrentLanguageRtl = languageManager.getCurrentAppLanguage().isRtl
        if (!isCurrentLanguageRtl && results.isArabic) {
            Constants.RTL_FIXER_SYMBOL + text
        }
        return text
    }

    private fun getHighlightColor(): Int {
        val codeColor = when (appearanceManager.getCurrentAppTheme()) {
            AppTheme.LIGHT -> "#1f96f2"
            AppTheme.NIGHT -> "#1f96f2"
            AppTheme.BROWN -> "#009688"
        }
        return Color.parseColor(codeColor)
    }

    class Holder(itemView: View) : RecyclerView.ViewHolder(itemView)

}