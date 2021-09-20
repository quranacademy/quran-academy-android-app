package org.quranacademy.quran.data.database.daos

import com.raizlabs.android.dbflow.config.FlowManager
import com.raizlabs.android.dbflow.sql.language.Delete
import com.raizlabs.android.dbflow.sql.language.SQLite
import org.quranacademy.quran.data.database.models.TranslationOrderModel
import javax.inject.Inject


class TranslationsOrderDao @Inject constructor() {

    private val adapter by lazy { FlowManager.getModelAdapter(TranslationOrderModel::class.java) }

    fun getTranslationsOrder(): List<TranslationOrderModel> {
        return SQLite.select().from(TranslationOrderModel::class.java).queryList()
    }

    fun saveTranslationsOrder(translations: List<TranslationOrderModel>) {
        adapter.saveAll(translations)
    }

    fun deleteAllRecords() {
        Delete().from(TranslationOrderModel::class.java).execute()
    }

}