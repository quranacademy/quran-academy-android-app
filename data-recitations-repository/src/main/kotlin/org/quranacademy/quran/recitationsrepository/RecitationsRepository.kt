package org.quranacademy.quran.recitationsrepository

import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.flow.asFlow
import org.quranacademy.quran.domain.models.FileDownloadInfo
import org.quranacademy.quran.domain.models.Recitation
import org.quranacademy.quran.domain.models.RecitationsList
import org.quranacademy.quran.recitationsrepository.downloading.AudioTimecodesDownloader
import org.quranacademy.quran.recitationsrepository.downloading.QuranAudioDownloadManager
import org.quranacademy.quran.recitationsrepository.downloading.RecitationAudioDownloadInfo
import org.quranacademy.quran.recitationsrepository.recitationaudioinfo.RecitationAudioInfo
import org.quranacademy.quran.recitationsrepository.recitationaudioinfo.RecitationAudioInfoDataSource
import org.quranacademy.quran.recitationsrepository.recitationaudioinfo.RecitationSurahsAudioInfo
import org.quranacademy.quran.recitationsrepository.recitations.RecitationsDataSource
import java.io.File
import javax.inject.Inject


class RecitationsRepository @Inject constructor(
        private val recitationsDataSource: RecitationsDataSource,
        private val recitationAudioInfoDataSource: RecitationAudioInfoDataSource,
        private val quranAudioDownloadManager: QuranAudioDownloadManager,
        private val audioTimecodesDownloader: AudioTimecodesDownloader,
        private val audioPathProvider: AudioPathProvider
) {

    private val audioFilesUpdatesChannel = BroadcastChannel<Unit>(1)

    suspend fun getRecitationInfo(recitationId: Long): RecitationSurahsAudioInfo {
        return recitationAudioInfoDataSource.getRecitationInfo(recitationId)
    }

    suspend fun getRecitations(): RecitationsList = recitationsDataSource.getRecitations()

    suspend fun getRecitationsAudioInfo(loadFromInternet: Boolean): List<RecitationAudioInfo> =
            recitationAudioInfoDataSource.getRecitationsAudioInfo(loadFromInternet)
                    .sortedBy { it.recitation.name }

    suspend fun downloadRecitationAudio(recitation: Recitation, onProgress: (RecitationAudioDownloadInfo) -> Unit) {
        quranAudioDownloadManager.downloadAllRecitationAudio(recitation, onProgress)
        audioFilesUpdatesChannel.send(Unit)
    }

    suspend fun downloadRecitationSurahAudio(
            recitationId: Long,
            surahNumber: Int,
            onProgress: (RecitationAudioDownloadInfo) -> Unit
    ) {
        val recitation = recitationsDataSource.getRecitation(recitationId)
        quranAudioDownloadManager.downloadRecitationSurahAudio(recitation, surahNumber, onProgress)
        audioFilesUpdatesChannel.send(Unit)
    }

    suspend fun cancelRecitationDownloading() {
        quranAudioDownloadManager.cancelAudioDownloading()
        audioFilesUpdatesChannel.send(Unit)
    }

    suspend fun deleteRecitationAudio(recitation: Recitation) {
        val recitationAudioFolder = audioPathProvider.getRecitationFolder(recitation.id)
        deleteDirectory(recitationAudioFolder)
        audioFilesUpdatesChannel.send(Unit)
    }

    suspend fun deleteRecitationSurahAudio(recitationId: Long, surahNumber: Int) {
        val surahAudioFolder = audioPathProvider.getRecitationSurahFolder(recitationId, surahNumber)
        deleteDirectory(surahAudioFolder)
        audioFilesUpdatesChannel.send(Unit)
    }

    fun getAudioFilesUpdates() = audioFilesUpdatesChannel.asFlow()

    suspend fun downloadRecitationTimecodes(recitation: Recitation, onProgress: (FileDownloadInfo) -> Unit) {
        audioTimecodesDownloader.downloadAudioTimeCodes(recitation, onProgress)
    }

    suspend fun cancelTimecodesDownloading() {
        audioTimecodesDownloader.cancelDownloading()
    }

    private fun deleteDirectory(path: File): Boolean {
        if (path.exists()) {
            val files = path.listFiles() ?: return false
            for (file in files) {
                if (file.isDirectory) {
                    deleteDirectory(file)
                } else {
                    val wasSuccessful = file.delete()
                    if (wasSuccessful) {
                        // Deleted successfully
                    }
                }
            }
        }
        return path.delete()
    }

}