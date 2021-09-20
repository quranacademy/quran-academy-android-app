package org.quranacademy.quran.data.database.adapters

import org.quranacademy.quran.domain.models.WordByWordTranslation

interface WordByWordTranslationDatabaseManager {

    fun connect()

    fun connectTo(translation: WordByWordTranslation)

    fun closeConnection()

    fun getCurrentTranslationAdapter(): WordByWordTranslationDatabaseAdapter?

    fun deleteTranslation(translation: WordByWordTranslation)

}