package org.quranacademy.quran.wordbywordrepository.database

import org.quranacademy.quran.data.PathProvider
import org.quranacademy.quran.data.database.adapters.WordByWordTranslationDatabaseFileManager
import org.quranacademy.quran.domain.models.WordByWordTranslation
import java.io.File
import javax.inject.Inject

class WordByWordTranslationDatabaseFileManagerImpl @Inject constructor(
        private val pathProvider: PathProvider
) : WordByWordTranslationDatabaseFileManager {

    override fun getTranslationDatabaseFile(translation: WordByWordTranslation): File {
        return File(pathProvider.databasesFolder, translation.fileName)
    }

    override fun deleteTranslationDatabaseFile(translation: WordByWordTranslation) {
        val databaseFile = getTranslationDatabaseFile(translation)
        databaseFile.delete()
        val databaseJournalFile = getTranslationDatabaseJoutnalFile(translation)
        databaseJournalFile.delete()
    }

    private fun getTranslationDatabaseJoutnalFile(translation: WordByWordTranslation): File {
        return File(pathProvider.databasesFolder, translation.fileName + "-journal")
    }

}