package org.quranacademy.quran.languagesrepository

import org.joda.time.Hours
import org.quranacademy.quran.data.database.daos.LanguagesDao
import org.quranacademy.quran.data.database.models.LanguageModel
import org.quranacademy.quran.data.prefs.AppPreferences
import org.quranacademy.quran.domain.commons.Clock
import timber.log.Timber
import javax.inject.Inject

class LanguagesCache @Inject constructor(
        private val appPreferences: AppPreferences,
        private val languagesDao: LanguagesDao,
        private val clock: Clock
) {

    fun isCacheEmpty(): Boolean = appPreferences.getLanguagesLastUpdateTime() == null

    fun isCacheExpired(): Boolean {
        return if (isCacheEmpty()) {
            true
        } else {
            val currentTime = clock.now()
            val languagesLastUpdateTime = appPreferences.getLanguagesLastUpdateTime()
            val periodAfterLastUpdateTime = Hours.hoursBetween(languagesLastUpdateTime, currentTime).hours
            periodAfterLastUpdateTime >= 1
        }
    }

    fun saveLanguages(newLanguageModels: List<LanguageModel>): List<LanguageModel> {
        val oldLanguageModels = languagesDao.getAllLanguages()
        deleteOldLanguages(oldLanguageModels, newLanguageModels)
        saveNewLanguages(oldLanguageModels, newLanguageModels)
        appPreferences.setLanguagesLastUpdateTime(clock.now())
        return newLanguageModels
    }

    fun getLanguages(): List<LanguageModel> {
        return languagesDao.getAllLanguages()
    }

    fun getLanguage(languageCode: String): LanguageModel {
        if (isCacheEmpty()) {
            throw LanguagesCacheIsEmptyException()
        }
        return languagesDao.getLanguage(languageCode)!!
    }

    private fun deleteOldLanguages(oldLanguageModels: List<LanguageModel>,
                                   newLanguageModels: List<LanguageModel>) {
        val newLanguageCodes = newLanguageModels.map { it.code }
        oldLanguageModels.filter { it.isDownloaded }
                .filter { !newLanguageCodes.contains(it.code) }
                .forEach {
                    //удаляем языки, которых нет на сервере
                    
                }
        languagesDao.deleteAllLanguages()
    }

    private fun saveNewLanguages(
            oldLanguageModels: List<LanguageModel>,
            newLanguageModels: List<LanguageModel>
    ) {
        val downloadedLanguageCodes = oldLanguageModels.filter { it.isDownloaded }.map { it.code }

        //переносим старые данные на новые переводы
        //здесь мы используем code как константу для каждого языка
        newLanguageModels
                .filter { downloadedLanguageCodes.contains(it.code) }
                .forEach { newLanguageModel ->
                    val oldLanguageModel = oldLanguageModels.first { it.code == newLanguageModel.code }
                    newLanguageModel.isDownloaded = oldLanguageModel.isDownloaded
                }

        //смотрим первый ли это запуск
        val isDataDownloaded = appPreferences.isInitialSetupCompleted()
        if (isDataDownloaded) {
            //при миграции с первой версии, когда языков не было, нужно отметить, что текущий язык уже загружен
            val currentLanguageCode = appPreferences.getAppLanguage()
            val currentLanguageModel = newLanguageModels.firstOrNull { it.code == currentLanguageCode }
            if (currentLanguageModel != null) {
                currentLanguageModel.isDownloaded = true
            }
        }

        Timber.i("Список языков сохранен в БД. Количество записей: ${newLanguageModels.size}")
        languagesDao.saveLanguages(newLanguageModels)
    }

}