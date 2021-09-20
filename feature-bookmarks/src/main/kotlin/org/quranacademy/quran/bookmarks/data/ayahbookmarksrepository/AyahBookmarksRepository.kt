package org.quranacademy.quran.bookmarks.data.ayahbookmarksrepository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.withContext
import org.joda.time.DateTime
import org.quranacademy.quran.bookmarks.domain.models.AyahBookmark
import org.quranacademy.quran.data.database.adapters.MushafPageBoundsDatabaseAdapter
import org.quranacademy.quran.data.database.daos.AyahsBookmarksDao
import org.quranacademy.quran.data.database.models.AyahBookmarkModel
import org.quranacademy.quran.domain.models.AyahId
import javax.inject.Inject

class AyahBookmarksRepository @Inject constructor(
        private val ayahBookmarksDao: AyahsBookmarksDao,
        private val mushafPageBoundsDatabaseAdapter: MushafPageBoundsDatabaseAdapter,
        private val mapper: AyahBookmarkDatabaseMapper
) {

    private val updatesChannel = BroadcastChannel<Unit>(1)

    fun getBookmarksUpdates() = updatesChannel.asFlow()

    suspend fun bookmarkAyah(
            ayahId: AyahId,
            folderId: Long
    ) = withContext(Dispatchers.IO) {
        val bookmark = AyahBookmarkModel(
                surahNumber = ayahId.surahNumber,
                ayahNumber = ayahId.ayahNumber,
                timestamp = DateTime.now().millis,
                folderId = folderId
        )
        ayahBookmarksDao.saveBookmark(bookmark)
        updatesChannel.send(Unit)
    }

    suspend fun removeAyahBookmark(ayahId: AyahId) {
        ayahBookmarksDao.deleteBookmark(ayahId.surahNumber, ayahId.ayahNumber)
        updatesChannel.send(Unit)
    }

    fun getAyahBookmark(ayahId: AyahId): AyahBookmark? {
        val bookmark = ayahBookmarksDao.getAyahBookmark(ayahId.surahNumber, ayahId.ayahNumber)
        return bookmark?.let { mapper.mapFrom(it) }
    }


    suspend fun getBookmarksList(): List<AyahBookmark> = withContext(Dispatchers.IO) {
        val bookmarkModels = ayahBookmarksDao.getAllBookmarks()
        mapper.mapFrom(bookmarkModels)
                .sortedWith(BookmarksComparator())
    }

    suspend fun deleteBookmarks(bookmarks: List<AyahBookmark>) = withContext(Dispatchers.IO) {
        val bookmarkModels = mapper.mapTo(bookmarks)
        ayahBookmarksDao.deleteBookmarks(bookmarkModels)
    }

    suspend fun deleteBookmarksInFolder(folderId: Long) {
        ayahBookmarksDao.deleteBookmarksInFolder(folderId)
    }

    suspend fun getBookmarksForPage(pageNumber: Int): List<AyahId> = withContext(Dispatchers.IO) {
        val pageAyahs = mushafPageBoundsDatabaseAdapter.getPageAyahs(pageNumber)
        val bookmarkModels = ayahBookmarksDao.getBookmarksForAyahs(pageAyahs)
        bookmarkModels.map { AyahId(it.surahNumber, it.ayahNumber) }
    }

}