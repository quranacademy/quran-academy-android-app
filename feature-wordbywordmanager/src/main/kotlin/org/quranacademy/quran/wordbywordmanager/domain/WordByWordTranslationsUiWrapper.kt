package org.quranacademy.quran.wordbywordmanager.domain

data class WordByWordTranslationsUiWrapper(
        val translations: List<WordByWordTranslationUIModel>,
        val currentTranslation: WordByWordTranslationUIModel?
)