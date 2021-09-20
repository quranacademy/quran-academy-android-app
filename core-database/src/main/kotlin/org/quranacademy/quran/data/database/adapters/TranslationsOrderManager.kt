package org.quranacademy.quran.data.database.adapters

import org.quranacademy.quran.data.database.models.TranslationOrderModel

interface TranslationsOrderManager {

    suspend fun onTranslationsListUpdated()

    fun getTranslationsOrder(): LinkedHashMap<String, TranslationOrderModel>

}