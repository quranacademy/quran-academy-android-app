package org.quranacademy.quran.wordbywordrepository

import org.joda.time.Hours
import org.quranacademy.quran.data.database.adapters.WordByWordTranslationDatabaseManager
import org.quranacademy.quran.data.database.daos.WordByWordTranslationsDao
import org.quranacademy.quran.data.database.models.WordByWordTranslationModel
import org.quranacademy.quran.data.prefs.AppPreferences
import org.quranacademy.quran.domain.commons.Clock
import org.quranacademy.quran.wordbywordrepository.database.WordByWordTranslationDatabaseMapper
import javax.inject.Inject

class WordByWordTranslationsCache @Inject constructor(
        private val appPreferences: AppPreferences,
        private val wordByWordTranslationsDao: WordByWordTranslationsDao,
        private val wordByWordTranslationDatabaseMapper: WordByWordTranslationDatabaseMapper,
        private val wordByWordTranslationDatabaseManager: WordByWordTranslationDatabaseManager,
        private val clock: Clock
) {

    fun isCacheEmpty(): Boolean = appPreferences.getWordByWordTranslationsLastUpdateTime() == null

    fun isCacheExpired(): Boolean {
        val lastUpdateTime = appPreferences.getWordByWordTranslationsLastUpdateTime()
        return if (lastUpdateTime == null) {
            true
        } else {
            val currentTime = clock.now()
            val periodAfterLastUpdateTime = Hours.hoursBetween(lastUpdateTime, currentTime).hours
            periodAfterLastUpdateTime >= 1
        }
    }

    fun saveTranslations(newTranslationModels: List<WordByWordTranslationModel>): List<WordByWordTranslationModel> {
        val oldTranslations = wordByWordTranslationsDao.getAllTranslations()

        deleteOldTranslations(oldTranslations, newTranslationModels)
        saveNewTranslations(newTranslationModels, oldTranslations)

        appPreferences.setWordByWordTranslationsLastUpdateTime(clock.now())
        return newTranslationModels
    }

    fun getTranslations(): List<WordByWordTranslationModel> {
        return wordByWordTranslationsDao.getTranslations()
    }

    fun getTranslationsWithUpdates(): List<WordByWordTranslationModel> {
        return wordByWordTranslationsDao.getTranslationsWithUpdates()
    }

    private fun deleteOldTranslations(oldTranslationModels: List<WordByWordTranslationModel>,
                                      newTranslationModels: List<WordByWordTranslationModel>) {
        val newTranslationCodes = newTranslationModels.map { it.languageCode }
        oldTranslationModels.filter { it.isDownloaded }
                .filter { !newTranslationCodes.contains(it.languageCode) }
                .map { wordByWordTranslationDatabaseMapper.mapFromDatabase(it) }
                .forEach {
                    //удаляем записи, которых нет на сервере
                    wordByWordTranslationDatabaseManager.deleteTranslation(it)
                }

        wordByWordTranslationsDao.deleteAllTranslations()
    }

    private fun saveNewTranslations(newTranslationModels: List<WordByWordTranslationModel>,
                                    oldTranslations: List<WordByWordTranslationModel>) {
        //переносим старые данные на новые переводы
        //здесь мы используем код языка как константу для каждого перевода
        val downloadedTranslationCodes = oldTranslations.filter { it.isDownloaded }.map { it.languageCode }
        newTranslationModels
                .filter { downloadedTranslationCodes.contains(it.languageCode) }
                .forEach { newTranslationModel ->
                    val oldTranslationModel = oldTranslations.first { it.languageCode == newTranslationModel.languageCode }
                    newTranslationModel.isDownloaded = oldTranslationModel.isDownloaded
                    newTranslationModel.localLastUpdateTime = oldTranslationModel.localLastUpdateTime
                }

        wordByWordTranslationsDao.saveTranslations(newTranslationModels)
    }

}