package org.quranacademy.quran.data.database.adapters

import org.quranacademy.quran.domain.models.Translation
import java.io.File

interface TranslationDatabaseFileManager {

    fun getTranslationDatabaseFile(translation: Translation): File

    fun deleteTranslationDatabaseFile(translation: Translation)

}