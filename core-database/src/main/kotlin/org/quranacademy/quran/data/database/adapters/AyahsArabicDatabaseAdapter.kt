package org.quranacademy.quran.data.database.adapters

import net.sqlcipher.Cursor
import org.quranacademy.quran.data.database.models.AyahModel

interface AyahsArabicDatabaseAdapter {

    fun connect()

    fun rawQuery(sql: String, selectionArgs: Array<String>?): Cursor

    fun getSurahAyahs(surahNumber: Int): List<AyahModel>

    fun getAyah(surahNumber: Int, ayahNumber: Int): AyahModel

    fun disconnect()

}