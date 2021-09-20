package org.quranacademy.quran.mainscreen.domain.bookmarkslist

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.quranacademy.quran.QuranConstants
import org.quranacademy.quran.bookmarks.data.ayahbookmarksrepository.AyahBookmarksRepository
import org.quranacademy.quran.bookmarks.data.folders.BookmarkFoldersRepository
import org.quranacademy.quran.bookmarks.data.pagebookmarksrepository.PageBookmarksRepository
import org.quranacademy.quran.bookmarks.data.readinghistory.LastReadingPlaceInfo
import org.quranacademy.quran.bookmarks.data.readinghistory.ReadingHistoryRepository
import org.quranacademy.quran.bookmarks.domain.models.AyahBookmark
import org.quranacademy.quran.bookmarks.domain.models.BookmarkFolder
import org.quranacademy.quran.bookmarks.domain.models.BookmarkItem
import org.quranacademy.quran.bookmarks.domain.models.PageBookmark
import org.quranacademy.quran.data.prefs.AppPreferences
import org.quranacademy.quran.domain.repositories.LanguagesRepository
import org.quranacademy.quran.extensions.mergeWith
import javax.inject.Inject

class BookmarksListInteractor @Inject constructor(
        private val appPreferences: AppPreferences,
        private val languagesRepository: LanguagesRepository,
        private val readingHistoryRepository: ReadingHistoryRepository,
        private val pageBookmarksRepository: PageBookmarksRepository,
        private val ayahBookmarksRepository: AyahBookmarksRepository,
        private val bookmarkFoldersRepository: BookmarkFoldersRepository
) {

    suspend fun getFolders(): List<BookmarkFolder> {
        return bookmarkFoldersRepository.getFoldersList()
    }

    suspend fun saveFolder(folder: BookmarkFolder) {
        bookmarkFoldersRepository.saveFolder(folder)
    }

    suspend fun deleteFolder(folder: BookmarkFolder) {
        bookmarkFoldersRepository.deleteFolder(folder)
        ayahBookmarksRepository.deleteBookmarksInFolder(folder.id)
    }

    fun getBookmarksUpdates() =
            readingHistoryRepository.getReadingHistoryUpdates()
                    .mergeWith(ayahBookmarksRepository.getBookmarksUpdates())
                    .mergeWith(pageBookmarksRepository.getBookmarksUpdates())

    fun getBookmarkFolderUpdates() = bookmarkFoldersRepository.getFoldersUpdates()

    fun getLanguageChanges() = languagesRepository.getLanguageChanges()

    suspend fun getBookmarksList(): BookmarksWrapper {
        val recentReadingPlaces = readingHistoryRepository.getRecentReadingPlaces()
        val pageBookmarks = pageBookmarksRepository.getBookmarksList()
        val ayahBookmarks = ayahBookmarksRepository.getBookmarksList()
        return BookmarksWrapper(
                recentReadingPlaces,
                pageBookmarks,
                ayahBookmarks
        )
    }

    suspend fun saveLastReadPositionForPage(pageNumber: Int) = withContext(Dispatchers.IO) {
        val pagePosition = pageNumber - 1
        val surahNumber = QuranConstants.SURAH_FOR_PAGE[pagePosition]
        val ayahNumber = QuranConstants.AYAH_FOR_PAGE[pagePosition]
        saveLastReadPosition(surahNumber, ayahNumber, true)
    }

    suspend fun saveLastReadPositionForAyah(surahNumber: Int, ayahNumber: Int) = withContext(Dispatchers.IO) {
        saveLastReadPosition(surahNumber, ayahNumber, false)
    }

    suspend fun deleteBookmarks(bookmarks: List<BookmarkItem>) {
        val ayahBookmarks = bookmarks.filter { it is AyahBookmark }.map { it as AyahBookmark }
        ayahBookmarksRepository.deleteBookmarks(ayahBookmarks)
        val pageBookmarks = bookmarks.filter { it is PageBookmark }.map { it as PageBookmark }
        pageBookmarksRepository.deleteBookmarks(pageBookmarks)
    }


    suspend fun saveLastReadPosition(surahNumber: Int, ayahNumber: Int, isMushafMode: Boolean) {
        val readSettings = appPreferences.getReadSettings()
        readSettings.isMushafMode = isMushafMode
        readSettings.lastReadSurah = surahNumber
        readSettings.lastReadAyah = ayahNumber
        appPreferences.saveReadSettings(readSettings)

        val recentReadingPlace = LastReadingPlaceInfo(surahNumber, ayahNumber, isMushafMode)
        readingHistoryRepository.addRecentReadingPlace(null, recentReadingPlace)
    }

}
