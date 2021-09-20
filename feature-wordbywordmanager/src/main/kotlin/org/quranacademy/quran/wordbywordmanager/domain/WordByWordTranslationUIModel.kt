package org.quranacademy.quran.wordbywordmanager.domain

data class WordByWordTranslationUIModel(
        val id: Long,
        val name: String,
        val fileName: String,
        val fileUrl: String,
        val languageCode: String,
        var isDownloaded: Boolean,
        var isEnabled: Boolean,
        var isUpdateAvailable: Boolean
)