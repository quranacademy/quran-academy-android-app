package org.quranacademy.quran.domain.models

data class Translation(
        val code: String,
        val name: String,
        val fileName: String,
        val fileUrl: String,
        val fileSize: Long,
        val languageCode: String,
        var isTafseer: Boolean,
        var isDownloaded: Boolean,
        var isEnabled: Boolean,
        val isUpdateAvailable: Boolean
) {

    val isArabic = languageCode == "ar"

}