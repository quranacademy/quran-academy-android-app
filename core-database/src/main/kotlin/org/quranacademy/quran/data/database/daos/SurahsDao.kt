package org.quranacademy.quran.data.database.daos

import com.raizlabs.android.dbflow.config.FlowManager
import com.raizlabs.android.dbflow.sql.language.SQLite
import org.quranacademy.quran.data.database.models.SurahModel
import org.quranacademy.quran.data.database.models.SurahModel_Table
import javax.inject.Inject

class SurahsDao @Inject constructor() {

    private val adapter by lazy { FlowManager.getModelAdapter(SurahModel::class.java) }

    fun saveSurahsList(surahs: List<SurahModel>) {
        adapter.saveAll(surahs)
    }

    fun getSurahs(): List<SurahModel> {
        return SQLite.select().from(SurahModel::class.java).queryList()
    }

    fun getSurahByNumber(surahNumber: Int): SurahModel {
        return SQLite.select().from(SurahModel::class.java)
                .where(SurahModel_Table.surah_number.`is`(surahNumber))
                .querySingle()!!
    }

}