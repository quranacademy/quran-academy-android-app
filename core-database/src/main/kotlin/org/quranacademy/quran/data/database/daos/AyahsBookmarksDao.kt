package org.quranacademy.quran.data.database.daos

import com.raizlabs.android.dbflow.config.FlowManager
import com.raizlabs.android.dbflow.kotlinextensions.and
import com.raizlabs.android.dbflow.sql.language.SQLite
import org.quranacademy.quran.data.database.models.AyahBookmarkModel
import org.quranacademy.quran.data.database.models.AyahBookmarkModel_Table
import org.quranacademy.quran.domain.models.AyahId
import javax.inject.Inject

class AyahsBookmarksDao @Inject constructor() {

    private val adapter by lazy { FlowManager.getModelAdapter(AyahBookmarkModel::class.java) }

    fun saveBookmark(bookmark: AyahBookmarkModel) {
        adapter.save(bookmark)
    }

    fun getAllBookmarks(): List<AyahBookmarkModel> {
        return SQLite.select().from(AyahBookmarkModel::class.java).queryList()
    }

    fun getBookmarksForAyahs(pageAyahs: List<AyahId>): List<AyahBookmarkModel> {
        val firstAyah = pageAyahs.first()
        var whereClause = AyahBookmarkModel_Table.surah_number.`is`(firstAyah.surahNumber)
                .and(AyahBookmarkModel_Table.ayah_number.`is`(firstAyah.ayahNumber))
        for (i in (1 until pageAyahs.size)) {
            val pageAyah = pageAyahs[i]
            whereClause = whereClause.or(
                    AyahBookmarkModel_Table.surah_number.`is`(pageAyah.surahNumber).and(
                            AyahBookmarkModel_Table.ayah_number.`is`(pageAyah.ayahNumber))
            )
        }
        return SQLite.select()
                .from(AyahBookmarkModel::class.java)
                .where(whereClause)
                .queryList()
    }

    fun isAyahBookmarked(surahNumber: Int, ayahNumber: Int): Boolean {
        return SQLite.select().from(AyahBookmarkModel::class.java)
                .where(AyahBookmarkModel_Table.surah_number.`is`(surahNumber)
                        and AyahBookmarkModel_Table.ayah_number.`is`(ayahNumber))
                .count() > 0
    }

    fun getAyahBookmark(surahNumber: Int, ayahNumber: Int): AyahBookmarkModel? {
        return SQLite.select().from(AyahBookmarkModel::class.java)
                .where(AyahBookmarkModel_Table.surah_number.`is`(surahNumber)
                        and AyahBookmarkModel_Table.ayah_number.`is`(ayahNumber))
                .querySingle()
    }

    fun deleteBookmark(surahNumber: Int, ayahNumber: Int) {
        SQLite.delete().from(AyahBookmarkModel::class.java)
                .where(AyahBookmarkModel_Table.surah_number.`is`(surahNumber)
                        and AyahBookmarkModel_Table.ayah_number.`is`(ayahNumber))
                .execute()

    }

    fun deleteBookmarks(bookmarks: List<AyahBookmarkModel>) {
        val bookmarkIds = bookmarks.map { it.id }
        SQLite.delete().from(AyahBookmarkModel::class.java)
                .where(AyahBookmarkModel_Table.id.`in`(bookmarkIds))
                .execute()
    }

    fun deleteBookmarksInFolder(folderId: Long) {
        SQLite.delete().from(AyahBookmarkModel::class.java)
                .where(AyahBookmarkModel_Table.folder_id.`is`(folderId))
                .execute()
    }

}