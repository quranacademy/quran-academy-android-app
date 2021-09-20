package org.quranacademy.quran.data.database.daos

import com.raizlabs.android.dbflow.config.FlowManager
import com.raizlabs.android.dbflow.sql.language.SQLite
import org.quranacademy.quran.data.database.models.RecitationModel
import org.quranacademy.quran.data.database.models.RecitationModel_Table
import javax.inject.Inject

class RecitationsDao @Inject constructor() {

    private val adapter by lazy { FlowManager.getModelAdapter(RecitationModel::class.java) }

    fun getRecitation(recitationId: Long): RecitationModel {
        return SQLite.select().from(RecitationModel::class.java)
                .where(RecitationModel_Table.id.eq(recitationId))
                .querySingle()!!
    }

    fun getRecitations(): List<RecitationModel> {
        return SQLite.select().from(RecitationModel::class.java).queryList()
    }

    fun saveRecitations(recitation: List<RecitationModel>) {
        return adapter.saveAll(recitation)
    }

}