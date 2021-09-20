package org.quranacademy.quran.domain.models

data class FileDownloadInfo(
        val downloadedSize: Long,
        val totalSize: Long
) {

    companion object {
        const val IS_FINISHED = -1L
    }

}