package org.quranacademy.sqliteciphergenerator.openhelpers

import android.database.sqlite.SQLiteDatabase

class NotEncryptedDatabase(databasePath: String) : DatabaseWrapper {

    private val db by lazy {
        SQLiteDatabase.openOrCreateDatabase(
                databasePath,
                null
        )
    }

    override fun execSQL(sql: String, args: Array<Any?>) {
        db.execSQL(sql, args)
    }

    override fun beginTransaction() {
        db.beginTransaction()
    }

    override fun setTransactionSuccessful() {
        db.setTransactionSuccessful()
    }

    override fun endTransaction() {
        db.endTransaction()
    }

    override fun close() {
        db.close()
    }

}