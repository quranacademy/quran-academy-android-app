package org.quranacademy.quran.recitationsrepository.downloading

import org.quranacademy.quran.domain.models.Recitation

class RecitationAudioDownloadInfo(
        val recitation: Recitation,
        val downloadedSizeBytes: Long,
        val totalSize: Long,
        val surahNumber: Int,
        val ayahNumber: Int
)