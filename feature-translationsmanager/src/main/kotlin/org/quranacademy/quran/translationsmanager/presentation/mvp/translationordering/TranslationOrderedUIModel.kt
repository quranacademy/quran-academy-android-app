package org.quranacademy.quran.translationsmanager.presentation.mvp.translationordering

import org.quranacademy.quran.translationsmanager.presentation.mvp.global.TranslationItem

data class TranslationOrderedUIModel(
        val translationCode: String,
        val name: String,
        val languageName: String,
        val showInDialog: Boolean,
        val order: Int
) : TranslationItem