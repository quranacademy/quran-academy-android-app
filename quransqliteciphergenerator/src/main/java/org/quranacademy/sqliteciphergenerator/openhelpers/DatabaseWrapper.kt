package org.quranacademy.sqliteciphergenerator.openhelpers

interface DatabaseWrapper {

    fun execSQL(sql: String, args: Array<Any?> = emptyArray())

    fun beginTransaction()

    fun setTransactionSuccessful()

    fun endTransaction()

    fun close()

}