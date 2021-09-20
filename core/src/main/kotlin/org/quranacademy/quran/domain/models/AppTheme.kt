package org.quranacademy.quran.domain.models

enum class AppTheme(val codeName: String) {

    LIGHT("light"), BROWN("brown"), NIGHT("night");

    companion object {
        fun findThemeByCodeName(codeName: String) = values().firstOrNull { it.codeName == codeName }
    }

}