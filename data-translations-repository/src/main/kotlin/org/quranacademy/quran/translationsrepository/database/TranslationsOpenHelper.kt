package org.quranacademy.quran.translationsrepository.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class TranslationsOpenHelper(
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

}