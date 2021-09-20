package org.quranacademy.quran.translationsmanager.domain

import org.quranacademy.quran.domain.models.TranslationOrder
import org.quranacademy.quran.domain.repositories.LanguagesRepository
import org.quranacademy.quran.domain.repositories.TranslationsOrderRepository
import org.quranacademy.quran.domain.repositories.TranslationsRepository
import javax.inject.Inject

class TranslationsOrderInteractor @Inject constructor(
        private val translationsRepository: TranslationsRepository,
        private val languagesRepository: LanguagesRepository,
        private val translationsOrderRepository: TranslationsOrderRepository
) {

    suspend fun getEnabledTranslations() = translationsRepository.getEnabledTranslations()

    suspend fun getLanguages() = languagesRepository.getLanguages(false).languages

    suspend fun getTranslationsOrder(): List<TranslationOrder> {
        return translationsOrderRepository.getTranslationsOrder()
    }

    suspend fun saveTranslationsOrder(translationsOrder: List<TranslationOrder>) {
        translationsOrderRepository.saveTranslationsOrder(translationsOrder)
    }

}
