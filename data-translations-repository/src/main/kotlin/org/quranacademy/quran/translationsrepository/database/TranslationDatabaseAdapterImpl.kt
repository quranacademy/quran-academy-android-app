package org.quranacademy.quran.translationsrepository.database

import android.annotation.SuppressLint
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import com.raizlabs.android.dbflow.kotlinextensions.and
import com.raizlabs.android.dbflow.sql.language.SQLite
import org.quranacademy.quran.data.database.adapters.TranslationDatabaseAdapter
import org.quranacademy.quran.data.database.fillModel
import org.quranacademy.quran.data.database.fillModels
import org.quranacademy.quran.data.database.models.AyahTranslationModel
import org.quranacademy.quran.data.database.models.AyahTranslationModel_Table
import org.quranacademy.quran.domain.models.Translation
import timber.log.Timber

@SuppressLint("Recycle")
class TranslationDatabaseAdapterImpl(
        private val context: Context,
        private val databasePath: String,
        private val adapterTranslation: Translation
) : TranslationDatabaseAdapter {

    private val database: SQLiteDatabase by lazy { connect() }
    private var isDatabaseConnected = false

    override fun getTranslation(): Translation = adapterTranslation

    override fun getTranslationsForSurah(surahNumber: Int): List<AyahTranslationModel> {
        val query = SQLite.select().from(AyahTranslationModel::class.java)
                .where(AyahTranslationModel_Table.sura.`is`(surahNumber))
                .orderBy(AyahTranslationModel_Table.ayat, true)
                .toString()

        val translationsCursor = database.rawQuery(query, null)
        return translationsCursor.fillModels()
    }

    override fun getTranslationForAyah(surahNumber: Int, ayahNumber: Int): AyahTranslationModel {
        val query = SQLite.select().from(AyahTranslationModel::class.java)
                .where(AyahTranslationModel_Table.sura.`is`(surahNumber)
                        and AyahTranslationModel_Table.ayat.`is`(ayahNumber)).toString()
        val translationsCursor = database.rawQuery(query, null)
        return translationsCursor.fillModel()
    }

    override fun rawQuery(query: String, vararg args: String?): Cursor {
        return database.rawQuery(query, args)
    }

    override fun execSql(query: String) {
        database.execSQL(query)
    }

    override fun beginTransaction() {
        database.beginTransaction()
    }

    override fun endTransaction(isSuccessful: Boolean) {
        if (isSuccessful) {
            database.setTransactionSuccessful()
        }
        database.endTransaction()
    }

    override fun destroy() {
        if (isDatabaseConnected) {
            database.close()
        }
    }

    private fun connect(): SQLiteDatabase {
        Timber.i("Попытка подключения к БД c переводом (${adapterTranslation.name})")
        return TranslationsOpenHelper(context, databasePath).writableDatabase.also {
            isDatabaseConnected = true
        }
    }

}