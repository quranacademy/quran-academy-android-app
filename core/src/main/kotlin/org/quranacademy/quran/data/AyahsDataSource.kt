package org.quranacademy.quran.data

import org.quranacademy.quran.domain.models.Ayah

interface AyahsDataSource {

    fun getSurahAyahs(surahNumber: Int): List<Ayah>

}