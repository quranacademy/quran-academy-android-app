package org.quranacademy.quran.domain.models

enum class MushafPageType(val code: String) {
    MADANI("madani"), MADANI_NEW("madani_new");

    companion object {
        fun findFontByCode(code: String) = values().firstOrNull { it.code == code }
    }

}