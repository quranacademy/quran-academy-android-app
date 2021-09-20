package org.quranacademy.sqliteciphergenerator.openhelpers

class EncryptedDatabase(databasePath: String) : DatabaseWrapper {

    private val db by lazy {
        SQLiteDatabaseCipher.openOrCreateDatabase(
                databasePath,
                "GYydCzbbh)a%95p^l(@V%!idW",
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