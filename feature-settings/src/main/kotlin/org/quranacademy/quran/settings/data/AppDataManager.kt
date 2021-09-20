package org.quranacademy.quran.settings.data

import org.quranacademy.quran.data.database.adapters.AyahsArabicDatabaseAdapter
import org.quranacademy.quran.data.database.adapters.MushafPageBoundsDatabaseAdapter
import org.quranacademy.quran.data.database.adapters.TranslationDatabaseManager
import org.quranacademy.quran.domain.models.Translation
import java.io.File
import javax.inject.Inject

class AppDataManager @Inject constructor(
        private val translationManager: TranslationDatabaseManager,
        private val pageBoundsDatabaseAdapter: MushafPageBoundsDatabaseAdapter,
        private val arabicDatabaseAdapter: AyahsArabicDatabaseAdapter
) {

    fun moveDataTo(currentAppPath: String, destinationPath: String) {
        val enabledTranslations = disconnectDatabases()

        try {
            val foldersForCopying = listOf("images", "audio", "databases")
            foldersForCopying.forEach { folderName ->
                val source = File(currentAppPath, folderName)
                val destination = File(destinationPath, folderName)
                if (source.exists()) {
                    source.copyRecursively(destination)
                    source.deleteRecursively()
                }
            }
        } catch (error: Exception) {
            File(destinationPath).deleteRecursively()
        }

        connectToDatabases(enabledTranslations)
    }

    private fun disconnectDatabases(): List<Translation> {
        arabicDatabaseAdapter
        pageBoundsDatabaseAdapter.disconnect()

        val enabledTranslations = translationManager.getAdapters()
                .map { it.getTranslation() }
        enabledTranslations.forEach {
            translationManager.closeConnection(it)
        }
        return enabledTranslations
    }

    private fun connectToDatabases(enabledTranslations: List<Translation>) {
        pageBoundsDatabaseAdapter.connect()
        enabledTranslations.forEach {
            translationManager.connectTo(it)
        }
    }

}