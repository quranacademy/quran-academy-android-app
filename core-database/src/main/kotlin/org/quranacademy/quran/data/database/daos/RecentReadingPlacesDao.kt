package org.quranacademy.quran.data.database.daos

import com.raizlabs.android.dbflow.config.FlowManager
import com.raizlabs.android.dbflow.kotlinextensions.and
import com.raizlabs.android.dbflow.sql.language.OrderBy
import com.raizlabs.android.dbflow.sql.language.SQLite
import org.quranacademy.quran.data.database.models.RecentReadingPlaceModel
import org.quranacademy.quran.data.database.models.RecentReadingPlaceModel_Table
import org.quranacademy.quran.data.prefs.AppPreferences
import javax.inject.Inject

class RecentReadingPlacesDao @Inject constructor(
        private val appPreferences: AppPreferences
) {

    private val adapter by lazy { FlowManager.getModelAdapter(RecentReadingPlaceModel::class.java) }

    fun removeFromHistory(surahNumber: Int, ayahNumber: Int) {
        SQLite.delete(RecentReadingPlaceModel::class.java)
                .where(RecentReadingPlaceModel_Table.surah.eq(surahNumber)
                        and RecentReadingPlaceModel_Table.ayah.eq(ayahNumber))
                .execute()
    }

    fun addToHistory(
            readingPlace: RecentReadingPlaceModel
    ) {
        //сохраняем новое место чтения
        adapter.save(readingPlace)

        //если записей больше трех, то удаляем более старые записи, оставив три последние
        val ids = SQLite.select(RecentReadingPlaceModel_Table.id)
                .from(RecentReadingPlaceModel::class.java)
                .orderBy(RecentReadingPlaceModel_Table.timestamp, false)
                .limit(appPreferences.getReadingHistorySize())
                .queryList()
                .map { it.id }
        SQLite.delete(RecentReadingPlaceModel::class.java)
                .where(RecentReadingPlaceModel_Table.id.notIn(ids))
                .execute()
    }

    fun getRecentReadingPlaces(): List<RecentReadingPlaceModel> {
        return SQLite.select()
                .from(RecentReadingPlaceModel::class.java)
                .orderBy(OrderBy.fromProperty(RecentReadingPlaceModel_Table.timestamp))
                .queryList()
    }

}