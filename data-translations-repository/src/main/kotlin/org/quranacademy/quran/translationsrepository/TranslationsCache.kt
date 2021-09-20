package org.quranacademy.quran.translationsrepository

import android.util.Log
import org.joda.time.Hours
import org.quranacademy.quran.data.database.adapters.TranslationDatabaseManager
import org.quranacademy.quran.data.database.daos.TranslationsDao
import org.quranacademy.quran.data.database.models.TranslationModel
import org.quranacademy.quran.data.prefs.AppPreferences
import org.quranacademy.quran.domain.commons.Clock
import org.quranacademy.quran.translationsrepository.database.TranslationDatabaseMapper
import timber.log.Timber
import javax.inject.Inject

class TranslationsCache @Inject constructor(
        private val appPreferences: AppPreferences,
        private val translationsDao: TranslationsDao,
        private val translationDatabaseManager: TranslationDatabaseManager,
        private val translationDatabaseMapper: TranslationDatabaseMapper,
        private val clock: Clock
) {

    fun isCacheExpired(): Boolean {
        val translationsLastUpdateTime = appPreferences.getTranslationsLastUpdateTime()
        val currentTime = clock.now()
        val periodAfterLastUpdateTime = Hours.hoursBetween(translationsLastUpdateTime, currentTime).hours
        return periodAfterLastUpdateTime >= 1
    }

    fun isCacheEmpty(): Boolean = appPreferences.getTranslationsLastUpdateTime() == null

    fun saveTranslations(newTranslationModels: List<TranslationModel>): List<TranslationModel> {
        val oldTranslationModels = translationsDao.getAllTranslations()
        deleteOldTranslations(oldTranslationModels, newTranslationModels)
        saveNewTranslations(newTranslationModels, oldTranslationModels)
        appPreferences.setTranslationsLastUpdateTime(clock.now())
        return newTranslationModels
    }

    fun getTranslations(): List<TranslationModel> {
        return translationsDao.getAllTranslations()
    }

    private fun deleteOldTranslations(
            oldTranslationModels: List<TranslationModel>,
            newTranslationModels: List<TranslationModel>
    ) {
        val newTranslationCodes = newTranslationModels.map { it.code }
        val translationsForDeleting = oldTranslationModels.filter { it.isDownloaded }
                .filter { !newTranslationCodes.contains(it.code) }

        Log.d("Translations", "New translations: ${newTranslationCodes.map { it }}")
        Log.d("Translations", "Deleting translations: ${translationsForDeleting.map { it.code }}")
        //удаляем переводы, которых нет на сервере
        translationDatabaseMapper.mapFromDatabase(translationsForDeleting)
                .forEach {
                    translationDatabaseManager.deleteTranslation(it)
                }

        translationsDao.deleteAllTranslations()
    }

    private fun saveNewTranslations(
            newTranslationModels: List<TranslationModel>,
            oldTranslationModels: List<TranslationModel>
    ) {
        val downloadedTranslationCodes = oldTranslationModels.filter { it.isDownloaded }.map { it.code }

        Log.d("Translations", "Downloaded translations: ${downloadedTranslationCodes.map { it }}")
        //переносим старые данные на новые переводы
        //здесь мы используем code как константу для каждого перевода
        newTranslationModels
                .filter { downloadedTranslationCodes.contains(it.code) }
                .also {
                    Log.d("Translations", "Updating translation: ${it.joinToString { it.code }}")
                }
                .forEach { newTranslationModel ->
                    val oldTranslationModel = oldTranslationModels.first { it.code == newTranslationModel.code }
                    newTranslationModel.isDownloaded = true
                    newTranslationModel.isEnabled = oldTranslationModel.isEnabled
                    newTranslationModel.localLastUpdateTime = oldTranslationModel.localLastUpdateTime
                }
        Timber.i("Список переводов сохранен в БД. Количество записей: ${newTranslationModels.size}")
        translationsDao.saveTranslations(newTranslationModels)
    }

    fun getTranslationsWithUpdates(): List<TranslationModel> {
        return translationsDao.getTranslationsWithUpdates()
    }

}