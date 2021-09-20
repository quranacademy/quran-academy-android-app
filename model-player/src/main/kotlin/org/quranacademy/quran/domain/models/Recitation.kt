package org.quranacademy.quran.domain.models

import java.io.Serializable

data class Recitation(
        val id: Long,
        val name: String,
        val urlDownloadTemplate: String,
        val timecodesFileUrl: String?,
        var isTimecodesDownloaded: Boolean
) : Serializable {

    fun hasTimecodes() = timecodesFileUrl != null

}