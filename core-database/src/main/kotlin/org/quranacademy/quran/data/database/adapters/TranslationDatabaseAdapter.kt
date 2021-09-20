package org.quranacademy.quran.data.database.adapters

import android.database.Cursor
import org.quranacademy.quran.data.database.models.AyahTranslationModel
import org.quranacademy.quran.domain.models.Translation

interface TranslationDatabaseAdapter {

    fun getTranslation(): Translation

    fun getTranslationsForSurah(surahNumber: Int): List<AyahTranslationModel>

    fun getTranslationForAyah(surahNumber: Int, ayahNumber: Int): AyahTranslationModel

    fun rawQuery(query: String, vararg args: String?): Cursor

    fun execSql(query: String)

    fun beginTransaction()

    fun endTransaction(isSuccessful: Boolean)

    fun destroy()

}