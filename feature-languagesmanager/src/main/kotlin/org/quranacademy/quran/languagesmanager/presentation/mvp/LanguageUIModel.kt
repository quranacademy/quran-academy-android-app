package org.quranacademy.quran.languagesmanager.presentation.mvp

data class LanguageUIModel(
        val code: String,
        val name: String,
        val englishName: String,
        val isRtl: Boolean,
        var isDownloaded: Boolean,
        var isEnabled: Boolean
)