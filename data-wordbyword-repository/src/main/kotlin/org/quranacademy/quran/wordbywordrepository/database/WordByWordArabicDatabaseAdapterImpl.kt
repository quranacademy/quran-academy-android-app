package org.quranacademy.quran.wordbywordrepository.database

import android.content.Context
import com.raizlabs.android.dbflow.config.FlowManager
import com.raizlabs.android.dbflow.kotlinextensions.and
import com.raizlabs.android.dbflow.sql.language.SQLite
import com.raizlabs.android.dbflow.sql.queriable.ModelQueriable
import com.raizlabs.android.dbflow.structure.database.FlowCursor
import net.sqlcipher.database.SQLiteDatabase
import org.quranacademy.quran.data.PathProvider
import org.quranacademy.quran.data.database.ArabicTextDatabaseOpenHelper
import org.quranacademy.quran.data.database.adapters.WordByWordDatabaseAdapter
import org.quranacademy.quran.data.database.models.AyahWordModel
import org.quranacademy.quran.data.database.models.AyahWordModel_Table
import timber.log.Timber
import javax.inject.Inject

class WordByWordArabicDatabaseAdapterImpl @Inject constructor(
        private val context: Context,
        private val pathProvider: PathProvider
) : WordByWordDatabaseAdapter {

    private val database: SQLiteDatabase by lazy { connect() }
    private var isDatabaseConnected = false
    private val modelAdapter by lazy { FlowManager.getModelAdapter(AyahWordModel::class.java) }

    override fun getWordsForSurah(surahNumber: Int): List<AyahWordModel> {
        val query = SQLite.select()
                .from(AyahWordModel::class.java)
                .where(AyahWordModel_Table.surah.`is`(surahNumber))
        return performQuery(query)
    }

    override fun getWordsForAyah(surahNumber: Int, ayahNumber: Int): List<AyahWordModel> {
        val query = SQLite.select()
                .from(AyahWordModel::class.java)
                .where(AyahWordModel_Table.surah.`is`(surahNumber)
                        and AyahWordModel_Table.ayah.`is`(ayahNumber))
        return performQuery(query)
    }

    private fun performQuery(query: ModelQueriable<AyahWordModel>): List<AyahWordModel> {
        val translationsCursor = database.rawQuery(query.toString(), null)
        val words = (1..translationsCursor.count).map {
            translationsCursor.moveToNext()
            val model = AyahWordModel()
            modelAdapter.loadFromCursor(FlowCursor.from(translationsCursor), model)
            return@map model
        }
        translationsCursor.close()
        return words
    }

    private fun connect(): SQLiteDatabase {
        Timber.i("Попытка подключения к БД Корана (пословный, арабский)")
        val databaseFile = pathProvider.quranArabicTextsDatabase
        return try {
            ArabicTextDatabaseOpenHelper(context, databaseFile.absolutePath).getDatabase()
                    .also {
                        isDatabaseConnected = true
                        Timber.i("Подключение прошло успешно (пословный, арабский)")
                    }
        } catch (e: Exception) {
            throw RuntimeException("Ошибка при подключении к БД (пословный, арабский)", e)
        }

    }

}