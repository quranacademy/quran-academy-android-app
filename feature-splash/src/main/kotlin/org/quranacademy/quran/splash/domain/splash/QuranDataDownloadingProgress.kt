package org.quranacademy.quran.splash.domain.splash

data class QuranDataDownloadingProgress(
        val text: String,
        val isIndeterminate: Boolean = true,
        val currentProgress: Long = 0,
        val maxProgress: Long = 0
)