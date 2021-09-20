package org.quranacademy.quran.mushafpageboundsrepository.database

import android.annotation.SuppressLint
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import com.raizlabs.android.dbflow.config.FlowManager
import com.raizlabs.android.dbflow.sql.language.Method
import com.raizlabs.android.dbflow.sql.language.SQLite
import org.quranacademy.quran.data.PathProvider
import org.quranacademy.quran.data.database.adapters.MushafPageBoundsDatabaseAdapter
import org.quranacademy.quran.data.database.fillModels
import org.quranacademy.quran.data.database.models.*
import org.quranacademy.quran.domain.models.AyahId
import timber.log.Timber
import java.io.File
import javax.inject.Inject
import kotlin.reflect.KClass

@SuppressLint("Recycle")
class MushafPageBoundsDatabaseAdapterImpl @Inject constructor(
        private val context: Context,
        private val pathProvider: PathProvider
) : MushafPageBoundsDatabaseAdapter {

    private lateinit var database: SQLiteDatabase

    override fun connect() {
        Timber.i("Попытка подключения к БД с координатами")
        val databasePath = pathProvider.mushafPageBoundsDatabase.absolutePath
        val databaseFile = File(databasePath)
        if (databaseFile.exists()) {
            database = PageBoundsDatabaseOpenHelper(context, databasePath).writableDatabase
            Timber.i("Подключение к БД с координатами прошло успешно")
        } else {
            Timber.w("Файл базы данных с координатами еще не загружен. Ошибка подключения")
        }
    }

    override fun getPageBounds(pageNumber: Int): PageBoundsModel {
        val query = SQLite.select(
                Method.min(AyahBoundsModel_Table.min_x),
                Method.min(AyahBoundsModel_Table.min_y),
                Method.max(AyahBoundsModel_Table.max_x),
                Method.max(AyahBoundsModel_Table.max_y)
        )
                .from(AyahBoundsModel::class.java)
                .where(AyahBoundsModel_Table.page_number.`is`(pageNumber))
                .toString()

        val resultCursor = database.rawQuery(query, null)
        resultCursor.moveToFirst()
        val minX = resultCursor.getInt(0)
        val minY = resultCursor.getInt(1)
        val maxX = resultCursor.getInt(2)
        val maxY = resultCursor.getInt(3)
        resultCursor.close()
        return PageBoundsModel(minX, minY, maxX, maxY)
    }

    override fun gePageAyahsBounds(pageNumber: Int): List<AyahBoundsModel> {
        val query = SQLite.select().from(AyahBoundsModel::class.java)
                .where(AyahBoundsModel_Table.page_number.`is`(pageNumber))
                .toString()

        val translationsCursor = database.rawQuery(query, null)
        return translationsCursor.fillModels()
    }

    override fun getAyahMarkerBounds(pageNumber: Int): List<AyahMarkerPositionModel> {
        if (!isTableExists(AyahMarkerPositionModel::class)) {
            return emptyList()
        }

        val query = SQLite.select().from(AyahMarkerPositionModel::class.java)
                .where(AyahBoundsModel_Table.page_number.`is`(pageNumber))
                .toString()

        val translationsCursor = database.rawQuery(query, null)
        return translationsCursor.fillModels()
    }

    override fun getSurahHeaderBounds(pageNumber: Int): List<SurahHeaderBoundsModel> {
        if (!isTableExists(SurahHeaderBoundsModel::class)) {
            return emptyList()
        }

        val query = SQLite.select().from(SurahHeaderBoundsModel::class.java)
                .where(AyahBoundsModel_Table.page_number.`is`(pageNumber))
                .toString()

        val translationsCursor = database.rawQuery(query, null)
        return translationsCursor.fillModels()
    }

    override fun getPageAyahs(pageNumber: Int): List<AyahId> {
        val query = SQLite.select()
                .from(AyahBoundsModel::class.java)
                .where(
                        AyahBoundsModel_Table.page_number.`is`(pageNumber)
                )
                .groupBy(
                        AyahBoundsModel_Table.page_number,
                        AyahBoundsModel_Table.sura_number,
                        AyahBoundsModel_Table.ayah_number
                )
                .orderBy(AyahBoundsModel_Table.page_number, false)
                .orderBy(AyahBoundsModel_Table.sura_number, false)
                .orderBy(AyahBoundsModel_Table.page_number, false)
                .toString()

        val pageAyahsCursor = database.rawQuery(query, null)
        return pageAyahsCursor.fillModels<AyahBoundsModel>()
                .map { AyahId(it.suraNumber, it.ayahNumber) }
    }

    override fun disconnect() {
        if (::database.isInitialized) {
            database.close()
        }
    }

    private fun isTableExists(tableClass: KClass<*>): Boolean {
        val tableName = FlowManager.getTableName(tableClass.java)
                .replace("`", "")
        val cursor = database.rawQuery(
                "SELECT DISTINCT tbl_name FROM sqlite_master WHERE tbl_name = \"$tableName\"",
                arrayOf()
        )
        val isTableExists = cursor.count > 0
        cursor.close()
        return isTableExists
    }

}