package org.quranacademy.quran.domain.models

data class WordByWordTranslationsWrapper(
        val translations: List<WordByWordTranslation>,
        val currentTranslationLanguageCode: String?
)