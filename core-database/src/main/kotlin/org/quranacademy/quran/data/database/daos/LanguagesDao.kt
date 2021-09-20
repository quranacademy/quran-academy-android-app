package org.quranacademy.quran.data.database.daos

import com.raizlabs.android.dbflow.config.FlowManager
import com.raizlabs.android.dbflow.sql.language.SQLite
import org.quranacademy.quran.data.database.models.LanguageModel
import org.quranacademy.quran.data.database.models.LanguageModel_Table
import javax.inject.Inject

class LanguagesDao @Inject constructor() {

    private val adapter by lazy { FlowManager.getModelAdapter(LanguageModel::class.java) }

    fun saveLanguages(translations: List<LanguageModel>) {
        return adapter.saveAll(translations)
    }

    fun getAllLanguages(): List<LanguageModel> {
        return SQLite.select().from(LanguageModel::class.java).queryList()
    }

    fun deleteAllLanguages() {
        SQLite.delete().from(LanguageModel::class.java).execute()
    }

    fun getLanguage(languageCode: String): LanguageModel? {
        return SQLite.select().from(LanguageModel::class.java)
                .where(LanguageModel_Table.code.`is`(languageCode))
                .querySingle()
    }

    fun markLanguageAsDownloaded(code: String, isDownloaded: Boolean) {
        val languageModel = getLanguage(code)!!
        languageModel.isDownloaded = isDownloaded
        adapter.save(languageModel)
    }

}