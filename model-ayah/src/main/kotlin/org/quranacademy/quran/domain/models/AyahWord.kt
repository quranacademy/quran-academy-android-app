package org.quranacademy.quran.domain.models

data class AyahWord(
        val position: Int,
        val arabicText: String,
        val arabicTextTajweed: String,
        val translationText: String?,
        val isSajdaWord: Boolean
) : AyahWordItem