package org.quranacademy.quran.wordbywordrepository.database

import android.content.Context
import org.quranacademy.quran.data.database.adapters.WordByWordTranslationDatabaseAdapter
import org.quranacademy.quran.data.database.adapters.WordByWordTranslationDatabaseFileManager
import org.quranacademy.quran.data.database.adapters.WordByWordTranslationDatabaseManager
import org.quranacademy.quran.data.database.daos.WordByWordTranslationsDao
import org.quranacademy.quran.data.prefs.AppPreferences
import org.quranacademy.quran.domain.models.WordByWordTranslation
import timber.log.Timber
import javax.inject.Inject

class WordByWordTranslationDatabaseManagerImpl @Inject constructor(
        private val context: Context,
        private val appPreferences: AppPreferences,
        private val wordByWordTranslationsDao: WordByWordTranslationsDao,
        private val wordByWordTranslationsDatabaseFileManager: WordByWordTranslationDatabaseFileManager,
        private val wordByWordTranslationDatabaseMapper: WordByWordTranslationDatabaseMapper
) : WordByWordTranslationDatabaseManager {

    private lateinit var currentTranslation: WordByWordTranslation
    private var currentTranslationAdapter: WordByWordTranslationDatabaseAdapter? = null

    override fun connect() {
        Timber.i("Инициализация менеджера с пословными переводами")
        Timber.i("Пословный включен: ${appPreferences.isWordByWordEnabled()}, " +
                "текущий пословный: ${appPreferences.getCurrentWbwTranslation()}")
        if (appPreferences.getCurrentWbwTranslation() != null) {
            val currentWordByWordTranslationCode = appPreferences.getCurrentWbwTranslation()
            if (currentWordByWordTranslationCode != null) {
                val currentTranslationModel = wordByWordTranslationsDao.getTranslationByLanguageCode(currentWordByWordTranslationCode)
                if (currentTranslationModel != null) {
                    val currentTranslation = wordByWordTranslationDatabaseMapper.mapFromDatabase(currentTranslationModel)
                    connectTo(currentTranslation)
                }
            }
        }
    }

    override fun connectTo(translation: WordByWordTranslation) {
        currentTranslationAdapter?.destroy()

        val databaseFile = wordByWordTranslationsDatabaseFileManager.getTranslationDatabaseFile(translation)
        currentTranslationAdapter = WordByWordTranslationDatabaseAdapterImpl(context, databaseFile.absolutePath, translation)
        currentTranslation = translation

        appPreferences.setCurrentWbwTranslation(translation.languageCode)
    }

    override fun closeConnection() {
        currentTranslationAdapter?.destroy()
    }

    override fun getCurrentTranslationAdapter(): WordByWordTranslationDatabaseAdapter? {
        return currentTranslationAdapter
    }

    override fun deleteTranslation(translation: WordByWordTranslation) {
        currentTranslationAdapter?.destroy()
        wordByWordTranslationsDatabaseFileManager.deleteTranslationDatabaseFile(translation)

        val currentTranslationAdapter = this.currentTranslationAdapter
        if (currentTranslationAdapter != null
                && currentTranslationAdapter.getTranslation() == translation) {
            this.currentTranslationAdapter = null
        }

        Timber.i("Пословный перевод \"${translation.name}\" удален.")
    }

}