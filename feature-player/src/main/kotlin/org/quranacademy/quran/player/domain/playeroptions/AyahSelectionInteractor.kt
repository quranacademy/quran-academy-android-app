package org.quranacademy.quran.player.domain.playeroptions

import org.quranacademy.quran.domain.models.Surah
import org.quranacademy.quran.domain.repositories.SurahsRepository
import javax.inject.Inject

class AyahSelectionInteractor @Inject constructor(
        private val surahsRepository: SurahsRepository
) {

    suspend fun getSurahsList(): List<Surah> {
        return surahsRepository.getSurahsList()
    }

}
