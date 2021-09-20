package org.quranacademy.quran.domain.models

data class Language(
        val code: String,
        val name: String,
        val englishName: String,
        val isRtl: Boolean,
        val isDownloaded: Boolean
)