package org.quranacademy.quran.translationsmanager.domain.translations

import org.quranacademy.quran.translationsmanager.presentation.mvp.global.TranslationItem

data class TranslationUIModel(
        val code: String,
        val languageName: String,
        val name: String,
        val fileName: String,
        val fileUrl: String,
        val fileSize: Long,
        val languageCode: String,
        val isTafseer: Boolean,
        val isDownloaded: Boolean,
        val isEnabled: Boolean,
        val isUpdateAvailable: Boolean,
        val isDownloading: Boolean
) : TranslationItem