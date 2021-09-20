package org.quranacademy.quran.qurandatarepository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.quranacademy.quran.data.AssetsFileExtractor
import org.quranacademy.quran.data.PathProvider
import org.quranacademy.quran.data.database.adapters.AyahsArabicDatabaseAdapter
import org.quranacademy.quran.data.prefs.AppPreferences
import org.quranacademy.quran.domain.repositories.QuranDataRepository
import timber.log.Timber
import javax.inject.Inject

class QuranDataRepositoryImpl @Inject constructor(
        private val appPreferences: AppPreferences,
        private val pathProvider: PathProvider,
        private val assetsFileExtractor: AssetsFileExtractor,
        private val ayahsArabicDatabaseAdapter: AyahsArabicDatabaseAdapter
) : QuranDataRepository {

    override suspend fun prepareQuranArabicTexts() = withContext(Dispatchers.IO) {
        copyDatabaseFileFromAssets()
        ayahsArabicDatabaseAdapter.connect()
    }

    override suspend fun isInitialSetupCompleted(): Boolean = withContext(Dispatchers.IO) {
        appPreferences.isInitialSetupCompleted()
    }

    override suspend fun setIsInitialSetupCompleted(isCompleted: Boolean) = withContext(Dispatchers.IO) {
        appPreferences.setIsInitialSetupCompleted(isCompleted)
    }

    private fun copyDatabaseFileFromAssets() {
        val isCurrentVersion = appPreferences.getCurrentQuranTextVersion() == CURRENT_QURAN_TEXT_VERSION
        val databaseFileIsExists = pathProvider.quranArabicTextsDatabase.exists()
        if (!isCurrentVersion || !databaseFileIsExists) {
            Timber.i("Копирование БД с текстами Корана с папки assets")
            assetsFileExtractor.extract("quran-arabic-text.db", pathProvider.quranArabicTextsDatabase, true)
            appPreferences.setCurrentQuranTextVersion(CURRENT_QURAN_TEXT_VERSION)
            Timber.i("Копирование БД с текстами Корана завершено")
        }
    }

    companion object {
        const val CURRENT_QURAN_TEXT_VERSION = 11
    }

}