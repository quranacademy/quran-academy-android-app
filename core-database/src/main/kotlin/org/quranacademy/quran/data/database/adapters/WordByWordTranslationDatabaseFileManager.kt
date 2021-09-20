package org.quranacademy.quran.data.database.adapters

import org.quranacademy.quran.domain.models.WordByWordTranslation
import java.io.File

interface WordByWordTranslationDatabaseFileManager {

    fun getTranslationDatabaseFile(translation: WordByWordTranslation): File

    fun deleteTranslationDatabaseFile(translation: WordByWordTranslation)

}