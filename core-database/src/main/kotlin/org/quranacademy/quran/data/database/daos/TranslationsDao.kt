package org.quranacademy.quran.data.database.daos

import com.raizlabs.android.dbflow.config.FlowManager
import com.raizlabs.android.dbflow.kotlinextensions.and
import com.raizlabs.android.dbflow.kotlinextensions.or
import com.raizlabs.android.dbflow.sql.language.SQLite
import org.quranacademy.quran.data.database.models.TranslationModel
import org.quranacademy.quran.data.database.models.TranslationModel_Table
import javax.inject.Inject

class TranslationsDao @Inject constructor() {

    private val adapter by lazy { FlowManager.getModelAdapter(TranslationModel::class.java) }

    fun saveTranslations(translations: List<TranslationModel>) {
        adapter.saveAll(translations)
    }

    fun getAllTranslations(): List<TranslationModel> {
        return SQLite.select().from(TranslationModel::class.java).queryList()
    }

    fun deleteAllTranslations() {
        SQLite.delete().from(TranslationModel::class.java).execute()
    }

    fun getEnabledTranslations(): List<TranslationModel> {
        return SQLite.select().from(TranslationModel::class.java)
                .where(TranslationModel_Table.is_downloaded.`is`(true)
                        and TranslationModel_Table.is_enabled.`is`(true))
                .queryList()
    }

    fun markTranslationAsDownloaded(code: String, isDownloaded: Boolean) {
        val translationModel = getByCode(code)
        translationModel.isDownloaded = isDownloaded
        translationModel.isEnabled = isDownloaded //при загрузке сразу включаем перевод, а при удалении отключаем
        if (isDownloaded) {
            translationModel.localLastUpdateTime = translationModel.remoteLastUpdateTime
        } else {
            translationModel.localLastUpdateTime = null
        }
        adapter.save(translationModel)
    }

    fun markTranslationAsEnabled(code: String, isEnabled: Boolean) {
        val translationModel = getByCode(code)
        translationModel.isEnabled = isEnabled
        adapter.save(translationModel)
    }

    fun markTranslationAsLastUpdated(code: String) {
        val translationModel = getByCode(code)
        translationModel.localLastUpdateTime = translationModel.remoteLastUpdateTime
        adapter.save(translationModel)
    }

    fun getTranslationsWithUpdates(): List<TranslationModel> {
        return SQLite.select().from(TranslationModel::class.java)
                .where(TranslationModel_Table.is_downloaded.`is`(true) and
                        TranslationModel_Table.local_last_update_time.isNull or
                        (TranslationModel_Table.local_last_update_time.isNotNull
                                and TranslationModel_Table.remote_last_update_time
                                .greaterThan(TranslationModel_Table.local_last_update_time))
                )
                .queryList()
    }

    private fun getByCode(code: String): TranslationModel {
        return SQLite.select().from(TranslationModel::class.java)
                .where(TranslationModel_Table.code.`is`(code))
                .querySingle()!!
    }

}