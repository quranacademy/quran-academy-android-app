package org.quranacademy.quran.domain.repositories

import kotlinx.coroutines.flow.Flow
import org.quranacademy.quran.domain.models.TranslationOrder

interface TranslationsOrderRepository {

    suspend fun getTranslationsOrder(): List<TranslationOrder>

    suspend fun saveTranslationsOrder(translationsOrder: List<TranslationOrder>)

    fun getTranslationOrderUpdates(): Flow<Unit>

}