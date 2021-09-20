package org.quranacademy.quran.search.presentation.adapter

import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import java.util.regex.Pattern

object ArabicTextHighlighter {

    private val ARABIC_VOWELS_REGEXP = "[" +
            "\\u064b\\u064c\\u064d\\u064e\\u064f\\u0650\\u0651\\u0652" +
            "\\u0653\\u06e1\\u08f0\\u0670" +
            "]*"
    private val ARABIC_REGEXP = "[\\p{InARABIC}]+((\\s*~)*(\\s*[\\p{InARABIC}]+)+)*"

    val ANY_ALIF_REGEXP = "[\\u0622\\u0623\\u0625\\u0627\\u0671]"
    val ANY_ALIF_REGEXP_LITERAL = "[\\\u0622\\\u0623\\\u0625\\\u0627\\\u0671]"
    val ANY_WAW_REGEXP = "[\\u0624\\u0648]"
    val ANY_WAW_REGEXP_LITERAL = "[\\\u0624\\\u0648]"
    val ANY_YEH_REGEXP = "[\\u0626\\u0649]"
    val ANY_YEH_REGEXP_LITERAL = "[\\\u0626\\\u0649]"

    fun createArabicRegex(query: String): Pattern {
        var regex = ""

        for (element in query) {
            regex = regex + element + ARABIC_VOWELS_REGEXP
        }

        regex = regex.replace(ANY_ALIF_REGEXP.toRegex(), ANY_ALIF_REGEXP_LITERAL)
                .replace(ANY_WAW_REGEXP.toRegex(), ANY_WAW_REGEXP_LITERAL)
                .replace(ANY_YEH_REGEXP.toRegex(), ANY_YEH_REGEXP_LITERAL)

        return Pattern.compile(regex)
    }

    fun highlightArabic(text: String, regex: Pattern, color: Int): Spannable {
        val spannableText = SpannableString(text)
        val matcher = regex.matcher(text)

        while (matcher.find()) {
            spannableText.setSpan(
                    ForegroundColorSpan(color),
                    matcher.start(),
                    matcher.end(),
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }
        return spannableText
    }

}