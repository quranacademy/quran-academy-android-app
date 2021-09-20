package org.quranacademy.quran.domain.models

data class AyahTranslation(
        val simpleText: String?,
        val textHQAFormat: HQATextFormat?,
        val translation: Translation
) {

    data class HQATextFormat(val text: String, val footnotes: Map<String, String>) {

        fun getFootnoteText(footnoteId: String): String = (footnotes[footnoteId])!!

    }

}