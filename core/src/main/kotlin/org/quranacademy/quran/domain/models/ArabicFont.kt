package org.quranacademy.quran.domain.models

enum class ArabicFont(
        val title: String,
        val codeName: String,
        val resCodeName: String
) {

    UTHMANIC_HAFS("KFGQPC Uthmanic Hafs", "uthmanic_hafs", "font_uthmanic_hafs");

    companion object {
        fun findFontByCodeName(codeName: String) = values().firstOrNull { it.codeName == codeName }
    }

}