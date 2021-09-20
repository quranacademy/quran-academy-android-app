package org.quranacademy.quran.memorization.domain

import org.quranacademy.quran.QuranConstants
import org.quranacademy.quran.domain.AyahPageFinder
import org.quranacademy.quran.domain.models.AyahId
import org.quranacademy.quran.domain.models.QuranPage
import org.quranacademy.quran.domain.models.Recitation
import org.quranacademy.quran.domain.repositories.QuranImagesRepository
import org.quranacademy.quran.domain.repositories.QuranPageRepository
import org.quranacademy.quran.memorization.data.AyahsRangeAudioDownloadManager
import org.quranacademy.quran.mushaf.domain.AyahsRangeFinder
import javax.inject.Inject

class MemorizationInteractor @Inject constructor(
        private val ayahsRangeFinder: AyahsRangeFinder,
        private val ayahPageFinder: AyahPageFinder,
        private val quranPageRepository: QuranPageRepository,
        private val imagesRepository: QuranImagesRepository,
        private val ayahsRangeAudioDownloadManager: AyahsRangeAudioDownloadManager
) {

    suspend fun getPageInfo(pageNumber: Int): QuranPage = quranPageRepository.getPageInfo(pageNumber)

    suspend fun getAyahPage(ayahId: AyahId): Int = ayahPageFinder.getPageForAyah(ayahId.surahNumber, ayahId.ayahNumber)

    fun getFirstPageAyah(pageNumber: Int): AyahId {
        val pageIndex = pageNumber - 1
        val pageSurah = QuranConstants.SURAH_FOR_PAGE[pageIndex]
        val pageAyah = QuranConstants.AYAH_FOR_PAGE[pageIndex]
        return AyahId(pageSurah, pageAyah)
    }

    suspend fun getLastPageAyah(pageNumber: Int) = ayahsRangeFinder.getLastPageAyah(pageNumber)

    fun isPageImageDownloaded(pageNumber: Int) = imagesRepository.getPageImageFile(pageNumber).exists()

    suspend fun getAyahsRange(start: AyahId, end: AyahId) = ayahsRangeFinder.getAyahsRange(start, end)

    suspend fun downloadPageImages(pagesForDownloading: List<Int>) {
        pagesForDownloading.forEach {
            imagesRepository.downloadPageImage(it)
        }
    }

    suspend fun downloadAyahsAudio(recitation: Recitation, ayahs: List<AyahId>) =
            ayahsRangeAudioDownloadManager.downloadAyahsAudio(recitation, ayahs, {})

}