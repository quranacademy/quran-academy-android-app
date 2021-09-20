package org.quranacademy.quran.translationsrepository.database

import android.content.Context
import org.quranacademy.quran.data.database.adapters.TranslationDatabaseAdapter
import org.quranacademy.quran.data.database.adapters.TranslationDatabaseFileManager
import org.quranacademy.quran.data.database.adapters.TranslationDatabaseManager
import org.quranacademy.quran.data.database.daos.TranslationsDao
import org.quranacademy.quran.domain.models.Translation
import timber.log.Timber
import javax.inject.Inject

class TranslationDatabaseManagerImpl @Inject constructor(
        private val context: Context,
        private val translationsDatabaseFileManager: TranslationDatabaseFileManager,
        translationsDao: TranslationsDao,
        translationDatabaseMapper: TranslationDatabaseMapper
) : TranslationDatabaseManager {

    private val adaptersMap = HashMap<String, TranslationDatabaseAdapter>()

    init {
        Timber.i("Инициализация менеджера с переводами аятов")
        val enabledTranslations = translationsDao.getEnabledTranslations()
        Timber.i("Включенные переводы: ${enabledTranslations.joinToString { it.code }}")
        translationDatabaseMapper.mapFromDatabase(enabledTranslations)
                .forEach { connectTo(it) }
    }

    override fun connectTo(translation: Translation) {
        val databasePath = translationsDatabaseFileManager.getTranslationDatabaseFile(translation).absolutePath
        val databaseAdapter = TranslationDatabaseAdapterImpl(context, databasePath, translation)
        adaptersMap[databaseAdapter.getTranslation().fileName] = databaseAdapter
    }

    override fun closeConnection(translation: Translation) {
        Timber.i("Закрытие подключения с БД перевода (${translation.name})")
        val databaseAdapter = adaptersMap.remove(translation.fileName)
        databaseAdapter?.destroy()
    }

    override fun getAdapters(): List<TranslationDatabaseAdapter> = adaptersMap.values.toList()

    override fun deleteTranslation(translation: Translation) {
        closeConnection(translation)
        translationsDatabaseFileManager.deleteTranslationDatabaseFile(translation)
    }

}