package org.quranacademy.quran.domain.models

data class WordByWordTranslation(
        val id: Long,
        val name: String,
        val fileName: String,
        val fileUrl: String,
        val languageCode: String,
        val isDownloaded: Boolean,
        val isUpdateAvailable: Boolean
)