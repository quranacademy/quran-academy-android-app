package org.quranacademy.quran.data.database.adapters

import org.quranacademy.quran.data.database.models.AyahWordModel

interface WordByWordDatabaseAdapter {

    fun getWordsForSurah(surahNumber: Int): List<AyahWordModel>

    fun getWordsForAyah(surahNumber: Int, ayahNumber: Int): List<AyahWordModel>

}