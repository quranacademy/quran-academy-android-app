package org.quranacademy.quran.memorization.mvp.memorization.gamemanager

import org.quranacademy.quran.domain.models.AyahId
import org.quranacademy.quran.memorization.domain.MemorizationInteractor
import org.quranacademy.quran.memorization.models.MemorizationMode
import org.quranacademy.quran.memorization.models.ModeData
import org.quranacademy.quran.memorization.models.PageModeData
import org.quranacademy.quran.memorization.models.SurahModeData
import javax.inject.Inject

class MemorizationAyahsRangeCalculator @Inject constructor(
        private val interactor: MemorizationInteractor
) {

    suspend fun getAyahsForMemorization(memorizationMode: MemorizationMode, modeData: ModeData): List<AyahId> {
        val ayahsRange = if (memorizationMode == MemorizationMode.PAGE) {
            val options = modeData as PageModeData
            val firstAyah = interactor.getFirstPageAyah(options.pageNumber)
            val lastAyah = interactor.getLastPageAyah(options.pageNumber)
            Pair(firstAyah, lastAyah)
        } else {
            val options = modeData as SurahModeData
            val firstAyah = AyahId(options.surahNumber, options.ayahsRange.first)
            val lastAyah = AyahId(options.surahNumber, options.ayahsRange.second)
            Pair(firstAyah, lastAyah)
        }

        return interactor.getAyahsRange(ayahsRange.first, ayahsRange.second)
    }

}