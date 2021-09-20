package org.quranacademy.quran.recitationsrepository.recitationaudioinfo

import org.quranacademy.quran.domain.models.Recitation
import java.io.Serializable

class RecitationAudioInfo(
        val recitation: Recitation,
        val downloadedSurahsCount: Int,
        val downloadedAudioSizeBytes: Long,
        val isFullyDownloaded: Boolean
) : Serializable