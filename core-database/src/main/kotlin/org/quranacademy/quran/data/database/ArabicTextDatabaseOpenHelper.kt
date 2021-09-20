package org.quranacademy.quran.data.database

import android.content.Context
import net.sqlcipher.database.SQLiteDatabase
import net.sqlcipher.database.SQLiteOpenHelper

class ArabicTextDatabaseOpenHelper(
        context: Context,
        filePath: String
) : SQLiteOpenHelper(context, filePath, null, 1) {

    override fun onCreate(db: SQLiteDatabase) {

    }

    override fun onOpen(db: SQLiteDatabase) {
        super.onOpen(db)

        db.rawQuery("pragma journal_mode=memory;", null).close()
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {

    }

    fun getDatabase(): SQLiteDatabase = getReadableDatabase("GYydCzbbh)a%95p^l(@V%!idW")

}