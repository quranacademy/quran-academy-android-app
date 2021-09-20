package org.quranacademy.quran.recitationsrepository.recitationaudioinfo

class SurahAudioInfo(
        val surahNumber: Int,
        val surahName: String,
        val downloadedAyahsNumber: Int,
        val downloadedAudioSizeBytes: Long,
        val isFullyDownloaded: Boolean
)