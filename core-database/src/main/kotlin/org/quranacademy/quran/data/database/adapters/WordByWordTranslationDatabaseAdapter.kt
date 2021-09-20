package org.quranacademy.quran.data.database.adapters

import org.quranacademy.quran.data.database.models.AyahWordTranslationModel
import org.quranacademy.quran.domain.models.WordByWordTranslation

interface WordByWordTranslationDatabaseAdapter {

    fun getTranslation(): WordByWordTranslation

    fun getSurahWordTranslations(surahNumber: Int): List<AyahWordTranslationModel>

    fun getAyahWordTranslations(surahNumber: Int, ayahNumber: Int): List<AyahWordTranslationModel>

    fun destroy()

}