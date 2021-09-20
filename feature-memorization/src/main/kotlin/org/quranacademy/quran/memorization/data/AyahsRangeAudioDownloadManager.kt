package org.quranacademy.quran.memorization.data

import org.quranacademy.quran.domain.models.AyahId
import org.quranacademy.quran.domain.models.Recitation
import org.quranacademy.quran.recitationsrepository.AudioPathProvider
import org.quranacademy.quran.recitationsrepository.downloading.AudioDownloadTask
import org.quranacademy.quran.recitationsrepository.downloading.QuranAudioDownloader
import org.quranacademy.quran.recitationsrepository.downloading.RecitationAudioDownloadInfo
import java.io.File
import javax.inject.Inject

class AyahsRangeAudioDownloadManager @Inject constructor(
        private val audioPathProvider: AudioPathProvider,
        private val quranAudioDownloader: QuranAudioDownloader
) {

    private var ayahsQueue: MutableList<AyahId>? = null
    private var currentDownloadableAyah: AyahId? = null
    private var currentDownloadTask: AudioDownloadTask? = null

    suspend fun downloadAyahsAudio(
            recitation: Recitation,
            ayahsQueue: List<AyahId>,
            onProgress: (RecitationAudioDownloadInfo) -> Unit
    ) {
        this.ayahsQueue = ayahsQueue.toMutableList()
        currentDownloadableAyah = ayahsQueue.first()
        startAudioDownloading(recitation, onProgress)
    }

    fun cancelAudioDownloading() {
        currentDownloadTask?.cancel()
        ayahsQueue = null
        currentDownloadableAyah = null
        currentDownloadTask = null
    }

    private suspend fun startAudioDownloading(
            recitation: Recitation,
            onProgress: (RecitationAudioDownloadInfo) -> Unit
    ) {
        currentDownloadableAyah?.let {
            val ayahFilePath = audioPathProvider.getAyahAudioPath(it.surahNumber, it.ayahNumber, recitation)
            val isAyahDownloaded = File(ayahFilePath).exists()
            if (isAyahDownloaded) {
                downloadNextAyah(recitation, it, onProgress)
            } else {
                downloadAyahAudio(recitation, it, onProgress)
            }
        }
    }

    private suspend fun downloadAyahAudio(
            recitation: Recitation,
            ayah: AyahId,
            onProgress: (RecitationAudioDownloadInfo) -> Unit
    ) {
        currentDownloadTask = quranAudioDownloader.downloadAudio(
                recitation = recitation,
                surahNumber = ayah.surahNumber,
                ayahNumber = ayah.ayahNumber,
                onProgressListener = {
                    val audioDownloadProgress = RecitationAudioDownloadInfo(
                            recitation = recitation,
                            downloadedSizeBytes = it.downloadedSize,
                            totalSize = it.totalSize,
                            surahNumber = ayah.surahNumber,
                            ayahNumber = ayah.ayahNumber
                    )
                    onProgress(audioDownloadProgress)
                }
        )
        currentDownloadTask?.start()
        downloadNextAyah(recitation, ayah, onProgress)
    }

    private suspend fun downloadNextAyah(
            recitation: Recitation,
            ayah: AyahId,
            onProgress: (RecitationAudioDownloadInfo) -> Unit
    ) {
        val queue = ayahsQueue
        if (queue != null) {
            val nextAyahIndex = queue.indexOf(ayah) + 1
            val isLastAyah = nextAyahIndex == queue.size
            if (!isLastAyah) {
                currentDownloadableAyah = queue[nextAyahIndex]
                startAudioDownloading(recitation, onProgress)
            }
        }
    }

}