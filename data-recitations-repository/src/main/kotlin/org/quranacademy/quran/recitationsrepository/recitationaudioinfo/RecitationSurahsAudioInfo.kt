package org.quranacademy.quran.recitationsrepository.recitationaudioinfo

import org.quranacademy.quran.domain.models.Recitation
import java.io.Serializable

class RecitationSurahsAudioInfo(
        val recitation: Recitation,
        val surahsAudioInfo: List<SurahAudioInfo>
) : Serializable