package org.quranacademy.quran.data.database.daos

import org.quranacademy.quran.data.database.adapters.AyahsArabicDatabaseAdapter
import org.quranacademy.quran.data.database.models.AyahModel
import javax.inject.Inject

class AyahsDao @Inject constructor(
        private val ayahsArabicDatabaseAdapter: AyahsArabicDatabaseAdapter
) {

    fun getSurahAyahs(surahNumber: Int): List<AyahModel> {
        return ayahsArabicDatabaseAdapter.getSurahAyahs(surahNumber)
    }

    fun getAyah(surahNumber: Int, ayahNumber: Int): AyahModel {
        return ayahsArabicDatabaseAdapter.getAyah(surahNumber, ayahNumber)
    }

}