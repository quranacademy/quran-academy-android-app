package org.quranacademy.quran.audiomanager.domain

import org.quranacademy.quran.recitationsrepository.RecitationsRepository
import org.quranacademy.quran.recitationsrepository.downloading.RecitationAudioDownloadInfo
import org.quranacademy.quran.recitationsrepository.recitationaudioinfo.RecitationSurahsAudioInfo
import javax.inject.Inject

class RecitationInfoInteractor @Inject constructor(
        private val recitationsRepository: RecitationsRepository
) {

    fun getAudioFilesUpdates() = recitationsRepository.getAudioFilesUpdates()

    suspend fun getRecitationsInfo(recitationId: Long): RecitationSurahsAudioInfo {
        return recitationsRepository.getRecitationInfo(recitationId)
    }

    suspend fun downloadRecitationSurahAudio(
            recitationId: Long,
            surahNumber: Int,
            onProgress: (RecitationAudioDownloadInfo) -> Unit
    ) {
        return recitationsRepository.downloadRecitationSurahAudio(recitationId, surahNumber, onProgress)
    }

    suspend fun cancelSurahAudioDownloading() {
        recitationsRepository.cancelRecitationDownloading()
    }

    suspend fun deleteRecitationSurahAudio(recitationId: Long, surahNumber: Int) {
        return recitationsRepository.deleteRecitationSurahAudio(recitationId, surahNumber)
    }

}
