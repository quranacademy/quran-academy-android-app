package org.quranacademy.quran.bookmarks.data.pagebookmarksrepository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.withContext
import org.joda.time.DateTime
import org.quranacademy.quran.bookmarks.domain.models.PageBookmark
import org.quranacademy.quran.data.database.daos.PageBookmarksDao
import org.quranacademy.quran.data.database.daos.SurahsNameTranslationsDao
import org.quranacademy.quran.data.database.models.PageBookmarkModel
import javax.inject.Inject

class PageBookmarksRepository @Inject constructor(
        private val pageBookmarksDao: PageBookmarksDao,
        private val surahsNameTranslationsDao: SurahsNameTranslationsDao,
        private val pageBookmarkDatabaseMapper: PageBookmarkDatabaseMapper
) {

    private val updatesChannel = BroadcastChannel<Unit>(1)

    fun getBookmarksUpdates() = updatesChannel.asFlow()

    suspend fun setBookmarked(pageNumber: Int, isBookmarked: Boolean) = withContext(Dispatchers.IO) {
        if (isBookmarked) {
            val bookmark = PageBookmarkModel(
                    pageNumber = pageNumber,
                    timestamp = DateTime.now().millis
            )
            bookmark.save()
        } else {
            pageBookmarksDao.deleteBookmark(pageNumber)
        }
        updatesChannel.send(Unit)
    }

    suspend fun getBookmarksList(): List<PageBookmark> = withContext(Dispatchers.IO) {
        val bookmarksList = pageBookmarksDao.getAllBookmarks()
        val surahNames = surahsNameTranslationsDao.getAllSurahNames()
        pageBookmarkDatabaseMapper.mapFrom(bookmarksList, surahNames)
                .sortedBy { it.pageNumber }
    }

    suspend fun deleteBookmarks(bookmarks: List<PageBookmark>) = withContext(Dispatchers.IO) {
        val bookmarkModels = pageBookmarkDatabaseMapper.mapTo(bookmarks)
        pageBookmarksDao.deleteBookmarks(bookmarkModels)
    }

}