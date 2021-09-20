package org.quranacademy.quran.mushaf.domain.page

import org.quranacademy.quran.bookmarks.data.ayahbookmarksrepository.AyahBookmarksRepository
import org.quranacademy.quran.domain.models.AyahId
import org.quranacademy.quran.domain.models.FileDownloadInfo
import org.quranacademy.quran.domain.models.PageBounds
import org.quranacademy.quran.domain.models.QuranPage
import org.quranacademy.quran.domain.repositories.QuranImagesRepository
import org.quranacademy.quran.domain.repositories.QuranPageRepository
import javax.inject.Inject

class MushafPageInteractor @Inject constructor(
        private val imagesRepository: QuranImagesRepository,
        private val quranPageRepository: QuranPageRepository,
        private val bookmarksRepository: AyahBookmarksRepository,
        private val ayahByPositionFinder: AyahByPositionFinder
) {

    suspend fun getPageInfo(pageNumber: Int): QuranPage = quranPageRepository.getPageInfo(pageNumber)

    suspend fun downloadPageImage(pageNumber: Int, onProgress: (FileDownloadInfo) -> Unit) =
            imagesRepository.downloadPageImage(pageNumber, onProgress = onProgress)

    suspend fun getAyahIdByPosition(x: Int, y: Int, pageBounds: PageBounds): AyahId {
        return ayahByPositionFinder.getAyahIdByPosition(x, y, pageBounds.pageAyahsBounds)
    }

    suspend fun getPageBookmarks(pageNumber: Int): List<AyahId> {
        return bookmarksRepository.getBookmarksForPage(pageNumber)
    }

    fun getMushafTypeChangingUpdates() = imagesRepository.getMushafTypeChangingUpdates()

}
