package org.quranacademy.quran.data.database.adapters

import org.quranacademy.quran.domain.models.Translation

interface TranslationDatabaseManager {

    fun connectTo(translation: Translation)

    fun closeConnection(translation: Translation)

    fun getAdapters(): List<TranslationDatabaseAdapter>

    fun deleteTranslation(translation: Translation)

}