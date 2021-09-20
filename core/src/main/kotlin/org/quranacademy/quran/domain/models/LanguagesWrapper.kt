package org.quranacademy.quran.domain.models

data class LanguagesWrapper(
        val languages: List<Language>,
        val currentLanguageCode: String
)