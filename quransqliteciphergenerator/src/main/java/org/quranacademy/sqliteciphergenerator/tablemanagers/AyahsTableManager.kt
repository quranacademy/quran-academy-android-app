package org.quranacademy.sqliteciphergenerator.tablemanagers

import net.sqlcipher.database.SQLiteDatabase
import org.quranacademy.sqliteciphergenerator.MainActivity
import org.quranacademy.sqliteciphergenerator.openhelpers.DatabaseWrapper

object AyahsTableManager {

    const val ID_COLUMN = "id"
    const val SURAH_COLUMN = "surah"
    const val AYAH_COLUMN = "ayah"
    const val TEXT_COLUMN = "text"
    const val TEXT_UTHMANIC_COLUMN = "text_uthmanic"

    fun createTable(database: DatabaseWrapper) {
        database.execSQL(
                "CREATE TABLE IF NOT EXISTS \"ayahs\" (" +
                        "`$ID_COLUMN` INTEGER NOT NULL," +
                        "`$SURAH_COLUMN` INTEGER NOT NULL," +
                        "`$AYAH_COLUMN` INTEGER NOT NULL," +
                        "`$TEXT_COLUMN` TEXT NOT NULL," +
                        "`$TEXT_UTHMANIC_COLUMN` TEXT NOT NULL" +
                        ")"
        )

        database.execSQL(
                "CREATE VIRTUAL TABLE ayahs_search_text USING FTS3(" +
                        "$ID_COLUMN INTEGER NOT NULL," +
                        "$SURAH_COLUMN INTEGER, " +
                        "$AYAH_COLUMN INTEGER, " +
                        "text TEXT, " +
                        "PRIMARY KEY(sura, ayah)" +
                        ")"
        )
    }

    fun clearTable(database: SQLiteDatabase) {
        database.execSQL("DELETE FROM ayahs")
        database.execSQL("DELETE FROM ayhs_search_text")
    }

    fun copyAyahs(
            hqaDatabase: android.database.sqlite.SQLiteDatabase,
            encDb: DatabaseWrapper,
            progressListener: (MainActivity.Progress) -> Unit
    ) {
        val hqaAyahsCursor = hqaDatabase.rawQuery("SELECT * FROM ayahs", null)
        hqaAyahsCursor.moveToFirst()

        var ayahId = 0
        val suraColumnIndex = hqaAyahsCursor.getColumnIndex("surah")
        val ayahColumnIndex = hqaAyahsCursor.getColumnIndex("ayah")
        val textColumnIndex = hqaAyahsCursor.getColumnIndex("text")
        val textUthmanicColumnIndex = hqaAyahsCursor.getColumnIndex("text_uthmanic")
        //val textTajweedIndex = hqaAyahsCursor.getColumnIndex(TAJWEED_TEXT_COLUMN)
        val textSearchColumnIndex = hqaAyahsCursor.getColumnIndex("text_search")
        val ayahsCount = hqaAyahsCursor.count

        do {
            val surah = hqaAyahsCursor.getInt(suraColumnIndex)
            val ayah = hqaAyahsCursor.getInt(ayahColumnIndex)
            val text = hqaAyahsCursor.getString(textColumnIndex)
                    .replace("۞", "")
                    .replace("بِسۡمِ ٱللَّهِ ٱلرَّحۡمَـٰنِ ٱلرَّحِیمِ ", "")
            val uthmanicText = hqaAyahsCursor.getString(textUthmanicColumnIndex)
                    .replace("۞", "")
                    .replace("بِسۡمِ ٱللَّهِ ٱلرَّحۡمَـٰنِ ٱلرَّحِیمِ ", "")
            encDb.execSQL(
                    "INSERT INTO ayahs(" +
                            "`$ID_COLUMN`, " +
                            "`$SURAH_COLUMN`, " +
                            "`$AYAH_COLUMN`, " +
                            "`$TEXT_COLUMN`, " +
                            "`$TEXT_UTHMANIC_COLUMN`" +
                            ") VALUES(?, ?, ?, ?, ?)",
                    arrayOf(ayahId, surah, ayah, text, uthmanicText)
            )

            val searchText = hqaAyahsCursor.getString(textSearchColumnIndex)
                    .replace("بسم الله الرحمن الرحيم ", "")
            encDb.execSQL(
                    "INSERT INTO ayahs_search_text(" +
                            "`$ID_COLUMN`, " +
                            "`$SURAH_COLUMN`, " +
                            "`$AYAH_COLUMN`, " +
                            "`$TEXT_COLUMN` " +
                            ") VALUES(?, ?, ?, ?)",
                    arrayOf(ayahId, surah, ayah, searchText)
            )

            progressListener(MainActivity.Progress("Ayahs", ++ayahId, ayahsCount))
        } while (hqaAyahsCursor.moveToNext())
    }

}