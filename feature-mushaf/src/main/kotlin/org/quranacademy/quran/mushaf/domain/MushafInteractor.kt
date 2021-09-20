package org.quranacademy.quran.mushaf.domain

import org.quranacademy.quran.QuranConstants
import org.quranacademy.quran.bookmarks.data.ayahbookmarksrepository.AyahBookmarksRepository
import org.quranacademy.quran.bookmarks.data.folders.BookmarkFoldersRepository
import org.quranacademy.quran.bookmarks.data.pagebookmarksrepository.PageBookmarksRepository
import org.quranacademy.quran.bookmarks.data.readinghistory.LastReadingPlaceInfo
import org.quranacademy.quran.bookmarks.data.readinghistory.ReadingHistoryRepository
import org.quranacademy.quran.bookmarks.domain.models.BookmarkFolder
import org.quranacademy.quran.data.prefs.AppPreferences
import org.quranacademy.quran.data.prefs.AppearancePreferences
import org.quranacademy.quran.domain.AyahPageFinder
import org.quranacademy.quran.domain.models.AppTheme
import org.quranacademy.quran.domain.models.AyahDetails
import org.quranacademy.quran.domain.models.AyahId
import org.quranacademy.quran.domain.models.FileDownloadInfo
import org.quranacademy.quran.domain.repositories.AyahsRepository
import org.quranacademy.quran.domain.repositories.QuranImagesRepository
import org.quranacademy.quran.domain.repositories.QuranPageRepository
import org.quranacademy.quran.domain.repositories.TranslationsRepository
import javax.inject.Inject

class MushafInteractor @Inject constructor(
        private val ayahsRangeFinder: AyahsRangeFinder,
        private val ayahPageFinder: AyahPageFinder,
        private val imagesRepository: QuranImagesRepository,
        private val quranPageRepository: QuranPageRepository,
        private val ayahsRepository: AyahsRepository,
        private val translationsRepository: TranslationsRepository,
        private val pageBookmarksRepository: PageBookmarksRepository,
        private val ayahBookmarksRepository: AyahBookmarksRepository,
        private val bookmarkFoldersRepository: BookmarkFoldersRepository,
        private val readingHistoryRepository: ReadingHistoryRepository,
        private val appPreferences: AppPreferences,
        private val appearancePreferences: AppearancePreferences
) {

    suspend fun isImagesDownloadingNeeded(): Boolean =
            imagesRepository.isImagesDownloadingNeeded()

    suspend fun disableImagesBundleDownloadingSuggestion() =
            imagesRepository.disableImagesBundleDownloadingSuggestion()

    suspend fun cancelImagesBundleDownloading() =
            imagesRepository.cancelImagesBundleDownloading()

    suspend fun downloadImagesBundle(onProgress: (FileDownloadInfo) -> Unit) =
            imagesRepository.downloadImagesBundle(onProgress)

    suspend fun getPageInfo(pageNumber: Int) =
            quranPageRepository.getPageInfo(pageNumber)

    suspend fun getAyahDetails(ayahId: AyahId): AyahDetails {
        return ayahsRepository.getAyahDetails(ayahId, loadTranslations = false)
    }

    suspend fun setBookmarked(pageNumber: Int, isBookmarked: Boolean) =
            pageBookmarksRepository.setBookmarked(pageNumber, isBookmarked)

    suspend fun getAyahsRange(start: AyahId, end: AyahId) =
            ayahsRangeFinder.getAyahsRange(start, end)

    suspend fun getCurrentPageNumber(): Int {
        val readSettings = appPreferences.getReadSettings()
        return ayahPageFinder.getPageForAyah(readSettings.lastReadSurah, readSettings.lastReadAyah)
    }

    suspend fun getPageForAyah(surahNumber: Int, ayahNumber: Int): Int {
        return ayahPageFinder.getPageForAyah(surahNumber, ayahNumber)
    }

    suspend fun getPageLastAyahId(pageNumber: Int): AyahId = ayahsRangeFinder.getLastPageAyah(pageNumber)

    suspend fun saveLastReadPosition(pageNumber: Int, isMushafMode: Boolean = true) {
        val pagePosition = pageNumber - 1
        val surahNumber = QuranConstants.SURAH_FOR_PAGE[pagePosition]
        val ayahNumber = QuranConstants.AYAH_FOR_PAGE[pagePosition]
        saveLastReadPosition(surahNumber, ayahNumber, isMushafMode)
    }

    suspend fun saveLastReadPosition(surahNumber: Int, ayahNumber: Int, isMushafMode: Boolean) {
        saveRecentPlace(surahNumber, ayahNumber, isMushafMode)

        val readSettings = appPreferences.getReadSettings()
        readSettings.isMushafMode = isMushafMode
        readSettings.lastReadSurah = surahNumber
        readSettings.lastReadAyah = ayahNumber

        appPreferences.saveReadSettings(readSettings)
    }

    private suspend fun saveRecentPlace(surahNumber: Int, ayahNumber: Int, isMushafMode: Boolean) {
        val previousPage = appPreferences.getReadSettings().let {
            LastReadingPlaceInfo(
                    surahNumber = it.lastReadSurah,
                    ayahNumber = it.lastReadAyah,
                    isMushafMode = it.isMushafMode
            )
        }

        val newPage = LastReadingPlaceInfo(
                surahNumber = surahNumber,
                ayahNumber = ayahNumber,
                isMushafMode = isMushafMode
        )
        readingHistoryRepository.addRecentReadingPlace(previousPage, newPage)
    }

    suspend fun getBookmarkFolders() = bookmarkFoldersRepository.getFoldersList()

    suspend fun bookmarkAyah(ayahId: AyahId) {
        ayahBookmarksRepository.bookmarkAyah(ayahId, BookmarkFolder.DEFAULT_FOLDER_ID)
    }

    suspend fun removeAyahBookmark(ayahId: AyahId) {
        return ayahBookmarksRepository.removeAyahBookmark(ayahId)
    }

    suspend fun getEnabledTranslations() = translationsRepository.getEnabledTranslations()

    fun setMushafTheme(theme: AppTheme) {
        appearancePreferences.setCurrentMushafTheme(theme)
    }

    fun getMushafTypeChangingUpdates() = imagesRepository.getMushafTypeChangingUpdates()

}
