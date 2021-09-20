package org.quranacademy.quran.memorization.mvp.memorization.gamemanager

import org.quranacademy.quran.domain.models.AyahId
import org.quranacademy.quran.domain.models.Recitation
import org.quranacademy.quran.memorization.domain.MemorizationInteractor
import javax.inject.Inject

class MemorizationDataDownloader @Inject constructor(
        private val interactor: MemorizationInteractor
) {

    suspend fun downloadDataForMemorization(recitation: Recitation, ayahsRange: List<AyahId>) {
        val firstPage = interactor.getAyahPage(ayahsRange.first())
        val lastPage = interactor.getAyahPage(ayahsRange.last())
        val pagesForDownloading = (firstPage..lastPage).toList()
                .filter { !interactor.isPageImageDownloaded(it) }
        interactor.downloadAyahsAudio(recitation, ayahsRange)
        interactor.downloadPageImages(pagesForDownloading)
    }

}