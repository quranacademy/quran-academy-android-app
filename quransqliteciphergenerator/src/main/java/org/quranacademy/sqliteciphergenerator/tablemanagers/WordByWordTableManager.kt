package org.quranacademy.sqliteciphergenerator.tablemanagers

import android.database.sqlite.SQLiteDatabase
import org.quranacademy.sqliteciphergenerator.MainActivity
import org.quranacademy.sqliteciphergenerator.openhelpers.DatabaseWrapper

object WordByWordTableManager {

    const val SURAH_COLUMN = "surah"
    const val AYAH_COLUMN = "ayah"
    const val ORDER_COLUMN = "order"
    const val TEXT_COLUMN = "text"
    const val TAJWEED_TEXT_COLUMN = "text_tajweed"


    fun createTable(database: DatabaseWrapper) {
        database.execSQL(
                "CREATE TABLE IF NOT EXISTS \"words\" (" +
                        "`$SURAH_COLUMN` INTEGER NOT NULL," +
                        "`$AYAH_COLUMN` INTEGER NOT NULL," +
                        "`$ORDER_COLUMN` INTEGER NOT NULL," +
                        "`$TEXT_COLUMN` TEXT NOT NULL," +
                        "`$TAJWEED_TEXT_COLUMN` TEXT NOT NULL" +
                        ")"
        )
    }

    fun clearTable(database: DatabaseWrapper) {
        database.execSQL("DELETE FROM words")
    }

    fun copyWords(
            defDb: SQLiteDatabase,
            tajweedWordsDb: SQLiteDatabase,
            encDb: DatabaseWrapper,
            progressListener: (MainActivity.Progress) -> Unit
    ) {
        val wordsCursor = defDb.rawQuery("select * from words", null)
        wordsCursor.moveToFirst()

        val suraColumnIndexWord = wordsCursor.getColumnIndex("surah")
        val ayatColumnIndexWord = wordsCursor.getColumnIndex("ayah")
        val orderColumnIndexWord = wordsCursor.getColumnIndex("order")
        val textColumnIndexWord = wordsCursor.getColumnIndex("text")
        //val textTajweedColumnIndexWord = wordsCursor.getColumnIndex("text_tajweed")

        val ayahsTajweedCursor = tajweedWordsDb.rawQuery("SELECT * FROM word_by_word_translation", null)
        val textTajweedIndex = ayahsTajweedCursor.getColumnIndex("text")
        ayahsTajweedCursor.moveToFirst()

        val wordsCount = wordsCursor.count
        var currentWord = 0
        do {
            encDb.execSQL(
                    "INSERT INTO words(`$SURAH_COLUMN`, " +
                            "`$AYAH_COLUMN`, " +
                            "`$ORDER_COLUMN`, " +
                            "`$TEXT_COLUMN`, " +
                            "`$TAJWEED_TEXT_COLUMN`" +
                            ") " +
                            "VALUES(?, ?, ?, ?, ?)",
                    arrayOf(
                            wordsCursor.getInt(suraColumnIndexWord),
                            wordsCursor.getInt(ayatColumnIndexWord),
                            wordsCursor.getInt(orderColumnIndexWord),
                            wordsCursor.getString(textColumnIndexWord),
                            ayahsTajweedCursor.getString(textTajweedIndex)
                            //wordsCursor.getString(textTajweedColumnIndexWord)
                    )
            )
            progressListener(MainActivity.Progress("Words", ++currentWord, wordsCount))
            ayahsTajweedCursor.moveToNext()
        } while (wordsCursor.moveToNext())
    }

}