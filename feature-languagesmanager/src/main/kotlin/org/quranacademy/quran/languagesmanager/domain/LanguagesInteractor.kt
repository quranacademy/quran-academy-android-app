package org.quranacademy.quran.languagesmanager.domain

import org.quranacademy.quran.domain.models.Language
import org.quranacademy.quran.domain.models.LanguagesWrapper
import org.quranacademy.quran.domain.repositories.LanguagesRepository
import javax.inject.Inject

class LanguagesInteractor @Inject constructor(
        private val languagesRepository: LanguagesRepository
) {

    suspend fun getLanguages(forceLoad: Boolean = false): LanguagesWrapper {
        return languagesRepository.getLanguages(forceLoad)
    }

    suspend fun downloadLanguage(language: Language) = languagesRepository.downloadLanguageData(language)

    suspend fun enableLanguage(language: Language) = languagesRepository.setAppLanguage(language)

    suspend fun removeLanguage(language: Language) = languagesRepository.removeLanguage(language)

}
