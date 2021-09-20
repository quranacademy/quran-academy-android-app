package org.quranacademy.quran.ayahsrepository.database

import android.content.Context
import com.raizlabs.android.dbflow.kotlinextensions.and
import com.raizlabs.android.dbflow.sql.language.SQLite
import net.sqlcipher.Cursor
import net.sqlcipher.database.SQLiteDatabase
import org.quranacademy.quran.data.PathProvider
import org.quranacademy.quran.data.database.ArabicTextDatabaseOpenHelper
import org.quranacademy.quran.data.database.adapters.AyahsArabicDatabaseAdapter
import org.quranacademy.quran.data.database.fillModel
import org.quranacademy.quran.data.database.fillModels
import org.quranacademy.quran.data.database.models.AyahModel
import org.quranacademy.quran.data.database.models.AyahModel_Table
import timber.log.Timber
import javax.inject.Inject

class AyahsArabicDatabaseAdapterImpl @Inject constructor(
        private val context: Context,
        private val pathProvider: PathProvider
) : AyahsArabicDatabaseAdapter {

    private lateinit var database: SQLiteDatabase
    private var isDatabaseConnected = false

    override fun connect() {
        Timber.i("Попытка подключения к БД Корана (аяты, арабский)")
        val databaseFile = pathProvider.quranArabicTextsDatabase

        if (!databaseFile.exists()) {
            return
        }

        return try {
            database = ArabicTextDatabaseOpenHelper(context, databaseFile.absolutePath).getDatabase()
                    .also {
                        isDatabaseConnected = true
                        Timber.i("Подключение прошло успешно (аяты, арабский)")
                    }
        } catch (e: Exception) {
            throw RuntimeException("Ошибка при подключении к БД (аяты, арабский)", e)
        }
    }

    override fun disconnect() {
        database.close()
        isDatabaseConnected = false
    }

    override fun rawQuery(sql: String, selectionArgs: Array<String>?): Cursor {
        return database.rawQuery(sql, selectionArgs)
    }

    override fun getSurahAyahs(surahNumber: Int): List<AyahModel> {
        val query = SQLite.select().from(AyahModel::class.java)
                .where(AyahModel_Table.surah.`is`(surahNumber))
                .orderBy(AyahModel_Table.ayah, true)
                .toString()
        val translationsCursor = database.rawQuery(query, null)
        return translationsCursor.fillModels()
    }

    override fun getAyah(surahNumber: Int, ayahNumber: Int): AyahModel {
        val query = SQLite.select().from(AyahModel::class.java)
                .where(AyahModel_Table.surah.`is`(surahNumber)
                        and AyahModel_Table.ayah.`is`(ayahNumber)).toString()
        val translationsCursor = database.rawQuery(query, null)
        return translationsCursor.fillModel()
    }

}