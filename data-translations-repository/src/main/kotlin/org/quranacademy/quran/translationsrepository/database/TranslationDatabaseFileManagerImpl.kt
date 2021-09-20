package org.quranacademy.quran.translationsrepository.database

import org.quranacademy.quran.data.PathProvider
import org.quranacademy.quran.data.database.adapters.TranslationDatabaseFileManager
import org.quranacademy.quran.domain.models.Translation
import java.io.File
import javax.inject.Inject

class TranslationDatabaseFileManagerImpl @Inject constructor(
        private val pathProvider: PathProvider
) : TranslationDatabaseFileManager {

    override fun getTranslationDatabaseFile(translation: Translation): File {
        return File(pathProvider.databasesFolder, translation.fileName)
    }

    override fun deleteTranslationDatabaseFile(translation: Translation) {
        val databaseFile = getTranslationDatabaseFile(translation)
        databaseFile.delete()
        val databaseJournalFile = getTranslationDatabaseJournalFile(translation)
        databaseJournalFile.delete()
    }

    private fun getTranslationDatabaseJournalFile(translation: Translation): File {
        return File(pathProvider.databasesFolder, translation.fileName + "-journal")
    }

}