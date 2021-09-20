package org.quranacademy.quran.mushaf.domain.ayahtranslation

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.quranacademy.quran.data.prefs.AppPreferences
import org.quranacademy.quran.domain.models.AyahDetails
import org.quranacademy.quran.domain.models.AyahId
import org.quranacademy.quran.domain.repositories.AyahsRepository
import org.quranacademy.quran.domain.repositories.TranslationsOrderRepository
import org.quranacademy.quran.domain.repositories.TranslationsRepository
import org.quranacademy.quran.domain.repositories.WordByWordTranslationsRepository
import org.quranacademy.quran.extensions.mergeWith
import javax.inject.Inject

class AyahTranslationsInteractor @Inject constructor(
        private val appPreferences: AppPreferences,
        private val surahsRepository: AyahsRepository,
        private val translationsRepository: TranslationsRepository,
        private val translationsOrderRepository: TranslationsOrderRepository,
        private val wordByWordTranslationsRepository: WordByWordTranslationsRepository
) {

    suspend fun getEnabledTranslationsListChangedUpdates(): Flow<Unit> {
        return translationsRepository.getEnabledTranslationsListUpdates()
                .mergeWith(translationsOrderRepository.getTranslationOrderUpdates())
                .mergeWith(wordByWordTranslationsRepository.getEnabledTranslationsListUpdates())
                .mergeWith(wordByWordTranslationsRepository.getTranslationsListUpdates())
                .mergeWith(appPreferences.getWbwEnablingUpdates().map { Unit })
    }

    suspend fun getAyahTranslations(ayahs: List<AyahId>): List<AyahDetails> {
        val enabledTranslations = translationsRepository.getEnabledTranslations()
        val wordByWordEnabled = appPreferences.getCurrentWbwTranslation() != null
                && appPreferences.isWordByWordEnabled()

        val ayahDetails = ayahs
                .map { ayahId -> surahsRepository.getAyahDetails(ayahId) }

        return if (enabledTranslations.isNotEmpty() || wordByWordEnabled) {
            ayahDetails.sortedWith(compareBy(AyahDetails::surahNumber, AyahDetails::ayahNumber))
        } else {
            throw EnabledTranslationsNotFound()
        }
    }

}