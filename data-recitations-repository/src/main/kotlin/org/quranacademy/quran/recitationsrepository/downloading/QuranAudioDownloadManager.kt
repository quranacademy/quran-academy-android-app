package org.quranacademy.quran.recitationsrepository.downloading

import org.quranacademy.quran.QuranConstants
import org.quranacademy.quran.domain.models.Recitation
import org.quranacademy.quran.recitationsrepository.AudioPathProvider
import java.io.File
import javax.inject.Inject

class QuranAudioDownloadManager @Inject constructor(
        private val audioPathProvider: AudioPathProvider,
        private val quranAudioDownloader: QuranAudioDownloader
) {

    private var surahsDownloadQueue: MutableList<Int>? = null
    private var currentDownloadableSurah: Int? = null
    private var currentDownloadableAyah: Int? = null
    private var currentDownloadTask: AudioDownloadTask? = null

    suspend fun downloadAllRecitationAudio(
            recitation: Recitation,
            onProgress: (RecitationAudioDownloadInfo) -> Unit
    ) {
        surahsDownloadQueue = (1..QuranConstants.SURAHS_COUNT).toMutableList()
        currentDownloadableSurah = 1
        currentDownloadableAyah = 1
        startAudioDownloading(recitation, onProgress)
    }

    suspend fun downloadRecitationSurahAudio(
            recitation: Recitation,
            surahNumber: Int,
            onProgress: (RecitationAudioDownloadInfo) -> Unit
    ) {
        surahsDownloadQueue = mutableListOf(surahNumber)
        currentDownloadableSurah = surahNumber
        currentDownloadableAyah = 1
        startAudioDownloading(recitation, onProgress)
    }

    fun cancelAudioDownloading() {
        currentDownloadTask?.cancel()
        surahsDownloadQueue = null
        currentDownloadableSurah = null
        currentDownloadableAyah = null
    }

    private suspend fun startAudioDownloading(
            recitation: Recitation,
            onProgress: (RecitationAudioDownloadInfo) -> Unit
    ) {
        val surahNumber = currentDownloadableSurah
        val ayahNumber = currentDownloadableAyah
        if (surahNumber != null && ayahNumber != null) {
            val ayahFilePath = audioPathProvider.getAyahAudioPath(surahNumber, ayahNumber, recitation)
            val isAyahDownloaded = File(ayahFilePath).exists()
            if (isAyahDownloaded) {
                downloadNextAyah(recitation, surahNumber, ayahNumber, onProgress)
            } else {
                downloadAyahAudio(recitation, surahNumber, ayahNumber, onProgress)
            }
        }
    }

    private suspend fun downloadAyahAudio(
            recitation: Recitation,
            surahNumber: Int,
            ayahNumber: Int,
            onProgress: (RecitationAudioDownloadInfo) -> Unit
    ) {
        currentDownloadTask = quranAudioDownloader.downloadAudio(
                recitation = recitation,
                surahNumber = surahNumber,
                ayahNumber = ayahNumber,
                onProgressListener = {
                    val audioDownloadProgress = RecitationAudioDownloadInfo(
                            recitation = recitation,
                            downloadedSizeBytes = it.downloadedSize,
                            totalSize = it.totalSize,
                            surahNumber = surahNumber,
                            ayahNumber = ayahNumber
                    )
                    onProgress(audioDownloadProgress)
                }
        )
        currentDownloadTask?.start()
        downloadNextAyah(recitation, surahNumber, ayahNumber, onProgress)
    }

    private suspend fun downloadNextAyah(
            recitation: Recitation,
            surahNumber: Int,
            ayahNumber: Int,
            onProgress: (RecitationAudioDownloadInfo) -> Unit
    ) {
        val isDownloadTaskNotChanged = surahNumber == currentDownloadableSurah
                && ayahNumber == currentDownloadableAyah
        if (isDownloadTaskNotChanged) {
            val surahMaxAyah = QuranConstants.SURAH_AYAHS_NUMBER[surahNumber - 1]
            val isLastAyah = ayahNumber == surahMaxAyah
            if (isLastAyah) {
                downloadNextSurah(recitation, onProgress)
            } else {
                currentDownloadableAyah = ayahNumber + 1
                startAudioDownloading(recitation, onProgress)
            }
        }
    }

    private suspend fun downloadNextSurah(
            recitation: Recitation,
            onProgress: (RecitationAudioDownloadInfo) -> Unit
    ) {
        surahsDownloadQueue?.let { queue ->
            currentDownloadableSurah = queue.removeAt(0)
            currentDownloadableAyah = 1
            if (queue.isNotEmpty()) {
                startAudioDownloading(recitation, onProgress)
            }
        }
    }

}