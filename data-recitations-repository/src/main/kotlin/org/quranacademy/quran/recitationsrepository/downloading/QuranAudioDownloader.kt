package org.quranacademy.quran.recitationsrepository.downloading

import org.quranacademy.quran.domain.models.FileDownloadInfo
import org.quranacademy.quran.domain.models.Recitation
import org.quranacademy.quran.recitationsrepository.AudioPathProvider
import javax.inject.Inject

class QuranAudioDownloader @Inject constructor(
        private val pathProvider: AudioPathProvider
) {

    fun downloadAudio(
            surahNumber: Int,
            ayahNumber: Int,
            recitation: Recitation,
            onProgressListener: ((FileDownloadInfo) -> Unit)? = null
    ): AudioDownloadTask {
        val audioFileUrl = pathProvider.getAyahDownloadUrl(surahNumber, ayahNumber, recitation)
        val destinationDatabaseFilePath = pathProvider.getAyahAudioPath(surahNumber, ayahNumber, recitation)
        return AudioDownloadTask(audioFileUrl, destinationDatabaseFilePath, onProgressListener)
    }

}