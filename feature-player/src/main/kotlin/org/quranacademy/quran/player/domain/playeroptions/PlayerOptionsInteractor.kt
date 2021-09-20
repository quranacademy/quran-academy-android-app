package org.quranacademy.quran.player.domain.playeroptions

import org.quranacademy.quran.data.prefs.AppPreferences
import org.quranacademy.quran.domain.models.FileDownloadInfo
import org.quranacademy.quran.domain.models.Recitation
import org.quranacademy.quran.domain.models.RecitationsList
import org.quranacademy.quran.domain.models.Surah
import org.quranacademy.quran.domain.repositories.SurahsRepository
import org.quranacademy.quran.recitationsrepository.RecitationsRepository
import javax.inject.Inject

class PlayerOptionsInteractor @Inject constructor(
        private val surahsRepository: SurahsRepository,
        private val recitationsRepository: RecitationsRepository,
        private val appPreferences: AppPreferences
) {

    suspend fun getSurahsList(): List<Surah> = surahsRepository.getSurahsList()

    suspend fun getRecitations(): RecitationsList = recitationsRepository.getRecitations()

    suspend fun downloadRecitationTimecodes(recitation: Recitation, onProgress: (FileDownloadInfo) -> Unit) {
        recitationsRepository.downloadRecitationTimecodes(recitation, onProgress)
    }

    fun isWordByWordEnabled() = appPreferences.isWordByWordEnabled() && appPreferences.getCurrentWbwTranslation() != null

    fun setPlayerAutoScrollEnabled(isEnabled: Boolean) = appPreferences.setPlayerAutoScrollEnabled(isEnabled)

    fun isPlayerAutoScrollEnabled() = appPreferences.isPlayerAutoScrollEnabled()

    fun isWordsHighlightingEnabled() = appPreferences.isWordsHighlightingEnabled()

    fun setWordsHighlightingEnabled(isEnabled: Boolean) = appPreferences.setWordsHighlightingEnabled(isEnabled)

}
