package org.quranacademy.quran.data.translations

import org.quranacademy.quran.domain.models.AyahWordItem

interface WordByWordDataSource {

    fun isWordByWordEnabled(): Boolean

    fun getSurahWordByWord(surahNumber: Int, ayahsCount: Int): List<List<AyahWordItem>>

    fun getAyahWordByWord(surahNumber: Int, ayahNumber: Int): List<AyahWordItem>

}