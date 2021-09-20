package org.quranacademy.quran.mainscreen.domain

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.joda.time.Days
import org.joda.time.Hours
import org.quranacademy.quran.data.prefs.AppPreferences
import org.quranacademy.quran.domain.commons.Clock
import org.quranacademy.quran.domain.repositories.TranslationsRepository
import org.quranacademy.quran.domain.repositories.WordByWordTranslationsRepository
import javax.inject.Inject

class MainInteractor @Inject constructor(
        private val translationsRepository: TranslationsRepository,
        private val wordByWordTranslationsRepository: WordByWordTranslationsRepository,
        private val appPreferences: AppPreferences,
        private val clock: Clock
) {

    fun isNeededToShowTranslationsCopyrightDialog(): Boolean {
        return appPreferences.isTranslationsCopyrightDialogShowed()
    }

    fun disableTranslationsCopyrightDialogShowing() {
        appPreferences.setTranslationsCopyrightDialogShowed(true)
    }

    suspend fun isNeededToShowTranslationUpdatesNotification(): Boolean {
        val isCheckingEnabled = appPreferences.isTranslationUpdatesCheckingEnabled()

        if (!isCheckingEnabled) {
            return false
        }

        val currentTime = clock.now()
        val lastNotificationShowingTime = appPreferences.getTranslationUpdatesNotificationShowingTime()
        val isNeededToShowNotification = if (lastNotificationShowingTime != null) {
            val periodAfterLastShowingTime = Days.daysBetween(lastNotificationShowingTime, currentTime).days
            periodAfterLastShowingTime >= 10
        } else {
            true
        }

        if (!isNeededToShowNotification) {
            return false
        }

        val lastTranslationsUpdateTime = appPreferences.getTranslationsLastUpdateTime()
        val checkTranslationsForUpdatesFromServer = if (lastTranslationsUpdateTime != null) {
            val periodAfterLastShowingTime = Hours.hoursBetween(lastTranslationsUpdateTime, currentTime).hours
            periodAfterLastShowingTime >= 1
        } else {
            false
        }

        val lastWbwTranslationsUpdateTime = appPreferences.getWordByWordTranslationsLastUpdateTime()
        val checkWbwTranslationsForUpdatesFromServer = if (lastWbwTranslationsUpdateTime != null) {
            val periodAfterLastShowingTime = Hours.hoursBetween(lastWbwTranslationsUpdateTime, currentTime).hours
            periodAfterLastShowingTime >= 1
        } else {
            false
        }

        val isTranslationUpdatesAvailable = try {
            translationsRepository.isTranslationUpdatesAvailable(checkTranslationsForUpdatesFromServer)
        } catch (error: Exception) {
            return false
        }

        val isWbwTranslationUpdatesAvailable = try {
            wordByWordTranslationsRepository.isTranslationUpdatesAvailable(checkWbwTranslationsForUpdatesFromServer)
        } catch (error: Exception) {
            return false
        }

        return isTranslationUpdatesAvailable || isWbwTranslationUpdatesAvailable
    }

    fun deferTranslationUpdatesNotificationShowing() {
        appPreferences.setTranslationUpdatesNotificationShowingTime(clock.now())
    }

    suspend fun isCurrentModeMushaf() = withContext(Dispatchers.IO) {
        appPreferences.getReadSettings().isMushafMode
    }

}
