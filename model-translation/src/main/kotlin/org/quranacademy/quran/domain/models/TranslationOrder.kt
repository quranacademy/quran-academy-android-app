package org.quranacademy.quran.domain.models

data class TranslationOrder(
        val translationCode: String,
        val showInDialog: Boolean,
        val order: Int
)