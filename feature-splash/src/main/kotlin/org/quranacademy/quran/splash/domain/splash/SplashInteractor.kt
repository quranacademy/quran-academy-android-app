package org.quranacademy.quran.splash.domain.splash

import org.quranacademy.quran.core.ui.R
import org.quranacademy.quran.data.appinforepository.AppInfoRepository
import org.quranacademy.quran.data.prefs.AppPreferences
import org.quranacademy.quran.domain.commons.ResourcesManager
import org.quranacademy.quran.domain.models.AppInfo
import org.quranacademy.quran.domain.models.Language
import org.quranacademy.quran.domain.models.ReadSettings
import org.quranacademy.quran.domain.repositories.LanguagesRepository
import org.quranacademy.quran.domain.repositories.MushafPageBoundsRepository
import org.quranacademy.quran.domain.repositories.QuranDataRepository
import javax.inject.Inject

class SplashInteractor @Inject constructor(
        private val appInfo: AppInfo,
        private val appPreferences: AppPreferences,
        private val resourcesManager: ResourcesManager,
        private val appInfoRepository: AppInfoRepository,
        private val languagesRepository: LanguagesRepository,
        private val quranDataRepository: QuranDataRepository,
        private val mushafPageBoundsRepository: MushafPageBoundsRepository
) {

    suspend fun isAppUpdateNeeded(): Boolean =
            if (appInfo.isDebug) {
                false
            } else {
                appInfoRepository.isAppUpdateNeeded()
            }

    suspend fun isInitialSetupCompleted(): Boolean {
        quranDataRepository.prepareQuranArabicTexts()
        return quranDataRepository.isInitialSetupCompleted()
    }

    suspend fun getLanguages(): List<Language> = languagesRepository.getLanguages(true).languages

    suspend fun downloadData(language: Language, onProgress: (QuranDataDownloadingProgress) -> Unit) {
        fun createMessage(id: Int) = QuranDataDownloadingProgress(resourcesManager.getString(id))

        onProgress(createMessage(R.string.quran_data_downloading_started))
        // перед началом запросов к API нужно сохранить код языка,
        // т. к. он указывается в заголовке каждого запроса
        languagesRepository.setAppLanguage(language)
        languagesRepository.downloadLanguageData(language)


        onProgress(createMessage(R.string.preparing_quran_data))

        quranDataRepository.prepareQuranArabicTexts()

        onProgress(createMessage(R.string.downloading_required_data_for_mushaf))

        mushafPageBoundsRepository.downloadAyahBoundsData {
            onProgress(
                    QuranDataDownloadingProgress(
                            text = resourcesManager.getString(R.string.downloading_required_data_for_mushaf),
                            isIndeterminate = false,
                            currentProgress = it.downloadedSize,
                            maxProgress = it.totalSize
                    )
            )
        }
        mushafPageBoundsRepository.reconnectToBoundsDatabase()
        quranDataRepository.setIsInitialSetupCompleted(true)
    }


    suspend fun getReadSettings(): ReadSettings {
        return appPreferences.getReadSettings()
    }

}
