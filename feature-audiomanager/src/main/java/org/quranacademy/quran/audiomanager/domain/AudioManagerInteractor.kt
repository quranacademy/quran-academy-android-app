package org.quranacademy.quran.audiomanager.domain

import org.quranacademy.quran.domain.models.Recitation
import org.quranacademy.quran.recitationsrepository.RecitationsRepository
import org.quranacademy.quran.recitationsrepository.downloading.RecitationAudioDownloadInfo
import org.quranacademy.quran.recitationsrepository.recitationaudioinfo.RecitationAudioInfo
import javax.inject.Inject

class AudioManagerInteractor @Inject constructor(
        private val recitationsRepository: RecitationsRepository
) {

    fun getAudioFilesUpdates() = recitationsRepository.getAudioFilesUpdates()

    suspend fun getRecitationsInfoList(loadFromInternet: Boolean): List<RecitationAudioInfo> {
        return recitationsRepository.getRecitationsAudioInfo(loadFromInternet)
    }

    suspend fun downloadRecitationAudio(recitation: Recitation, onProgress: (RecitationAudioDownloadInfo) -> Unit) {
        return recitationsRepository.downloadRecitationAudio(recitation, onProgress)
    }

    suspend fun cancelRecitationDownloading() {
        return recitationsRepository.cancelRecitationDownloading()
    }

    suspend fun deleteRecitationAudio(recitation: Recitation) {
        return recitationsRepository.deleteRecitationAudio(recitation)
    }

}
