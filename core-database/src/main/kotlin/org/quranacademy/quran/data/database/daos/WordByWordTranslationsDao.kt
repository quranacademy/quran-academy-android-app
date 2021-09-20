package org.quranacademy.quran.data.database.daos

import com.raizlabs.android.dbflow.config.FlowManager
import com.raizlabs.android.dbflow.kotlinextensions.and
import com.raizlabs.android.dbflow.kotlinextensions.or
import com.raizlabs.android.dbflow.sql.language.SQLite
import org.quranacademy.quran.data.database.models.WordByWordTranslationModel
import org.quranacademy.quran.data.database.models.WordByWordTranslationModel_Table
import javax.inject.Inject

class WordByWordTranslationsDao @Inject constructor() {

    private val adapter by lazy { FlowManager.getModelAdapter(WordByWordTranslationModel::class.java) }

    fun saveTranslations(translations: List<WordByWordTranslationModel>) {
        adapter.saveAll(translations)
    }

    fun getAllTranslations(): List<WordByWordTranslationModel> {
        return SQLite.select().from(WordByWordTranslationModel::class.java).queryList()
    }

    fun deleteAllTranslations() {
        SQLite.delete().from(WordByWordTranslationModel::class.java).execute()
    }

    fun getTranslations(): List<WordByWordTranslationModel> {
        return SQLite.select().from(WordByWordTranslationModel::class.java).queryList()
    }

    fun markTranslationAsDownloaded(id: Long, isDownloaded: Boolean) {
        val translationModel = SQLite.select().from(WordByWordTranslationModel::class.java)
                .where(WordByWordTranslationModel_Table.id.`is`(id))
                .querySingle()!!
        translationModel.isDownloaded = isDownloaded
        if (isDownloaded) {
            translationModel.localLastUpdateTime = translationModel.remoteLastUpdateTime
        } else {
            translationModel.localLastUpdateTime = null
        }
        adapter.save(translationModel)
    }

    fun markTranslationAsLastUpdated(id: Long) {
        val translationModel = SQLite.select().from(WordByWordTranslationModel::class.java)
                .where(WordByWordTranslationModel_Table.id.`is`(id))
                .querySingle()!!
        translationModel.localLastUpdateTime = translationModel.remoteLastUpdateTime
        adapter.save(translationModel)
    }

    fun getTranslationByLanguageCode(code: String): WordByWordTranslationModel? {
        return SQLite.select().from(WordByWordTranslationModel::class.java)
                .where(WordByWordTranslationModel_Table.language_code.`is`(code))
                .querySingle()
    }

    fun getTranslationsWithUpdates(): List<WordByWordTranslationModel> {
        return SQLite.select().from(WordByWordTranslationModel::class.java)
                .where(WordByWordTranslationModel_Table.is_downloaded.`is`(true) and
                        WordByWordTranslationModel_Table.local_last_update_time.isNull or
                        (WordByWordTranslationModel_Table.local_last_update_time.isNotNull
                                and WordByWordTranslationModel_Table.remote_last_update_time
                                .greaterThan(WordByWordTranslationModel_Table.local_last_update_time))
                )
                .queryList()
    }

}