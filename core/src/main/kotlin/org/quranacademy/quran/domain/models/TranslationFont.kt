package org.quranacademy.quran.domain.models

enum class TranslationFont(
        val title: String,
        val codeName: String,
        val resCodeName: String
) {

    ROBOTO("Roboto", " roboto", "font_roboto_regular"),
    VERA_HUMANA("Vera Humana", "vera_humana", "font_vera_humana"),
    PT_SANS("PT Sans", "pt_sans", "font_pt_sans"),
    PT_SERIF("PT Serif", "pt_serif", "font_pt_serif");

    companion object {
        fun findFontByCodeName(codeName: String) = TranslationFont.values().firstOrNull { it.codeName == codeName }
    }

}