package org.quranacademy.quran.wordbywordrepository.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import com.raizlabs.android.dbflow.config.FlowManager
import com.raizlabs.android.dbflow.kotlinextensions.and
import com.raizlabs.android.dbflow.sql.language.SQLite
import com.raizlabs.android.dbflow.sql.queriable.ModelQueriable
import com.raizlabs.android.dbflow.structure.ModelAdapter
import com.raizlabs.android.dbflow.structure.database.FlowCursor
import org.quranacademy.quran.data.database.AppDatabase
import org.quranacademy.quran.data.database.adapters.WordByWordTranslationDatabaseAdapter
import org.quranacademy.quran.data.database.models.AyahWordTranslationModel
import org.quranacademy.quran.data.database.models.AyahWordTranslationModel_Table
import org.quranacademy.quran.domain.models.WordByWordTranslation
import timber.log.Timber

class WordByWordTranslationDatabaseAdapterImpl(
        private val context: Context,
        private val databasePath: String,
        private val translation: WordByWordTranslation
) : WordByWordTranslationDatabaseAdapter {

    private val database: SQLiteDatabase by lazy { connect() }
    private var isDatabaseConnected = false

    private var modelAdapter: ModelAdapter<AyahWordTranslationModel> = FlowManager.getDatabase(AppDatabase::class.java)
            .getModelAdapterForTable(AyahWordTranslationModel::class.java)!!

    override fun getTranslation(): WordByWordTranslation = translation

    override fun getSurahWordTranslations(surahNumber: Int): List<AyahWordTranslationModel> {
        val query = SQLite.select()
                .from(AyahWordTranslationModel::class.java)
                .where(AyahWordTranslationModel_Table.sura.`is`(surahNumber))
        return performQuery(query)
    }

    override fun getAyahWordTranslations(surahNumber: Int, ayahNumber: Int): List<AyahWordTranslationModel> {
        val query = SQLite.select()
                .from(AyahWordTranslationModel::class.java)
                .where(AyahWordTranslationModel_Table.sura.`is`(surahNumber)
                        and AyahWordTranslationModel_Table.ayat.`is`(ayahNumber))
        return performQuery(query)
    }

    override fun destroy() {
        if (isDatabaseConnected) {
            database.close()
        }
    }

    private fun performQuery(query: ModelQueriable<AyahWordTranslationModel>): List<AyahWordTranslationModel> {
        val translationsCursor = database.rawQuery(query.toString(), null)
        val translations = (1..translationsCursor.count).map {
            translationsCursor.moveToNext()
            val model = AyahWordTranslationModel()
            modelAdapter.loadFromCursor(FlowCursor.from(translationsCursor), model)
            return@map model
        }
        translationsCursor.close()
        return translations
    }

    private fun connect(): SQLiteDatabase {
        Timber.i("Подключение пословного перевода: ${translation.name}")
        return WordByWordTranslationsOpenHelper(context, databasePath).writableDatabase
                .also {
                    isDatabaseConnected = true
                    Timber.i("Перевод: ${translation.name} успешно подключен")
                }
    }

}