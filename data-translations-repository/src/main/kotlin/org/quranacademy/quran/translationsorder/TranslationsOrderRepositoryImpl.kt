package org.quranacademy.quran.translationsorder

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.withContext
import org.quranacademy.quran.data.database.daos.TranslationsOrderDao
import org.quranacademy.quran.domain.models.TranslationOrder
import org.quranacademy.quran.domain.repositories.TranslationsOrderRepository
import javax.inject.Inject

class TranslationsOrderRepositoryImpl @Inject constructor(
        private val translationsOrderDao: TranslationsOrderDao,
        private val translationsOrderDatabaseMapper: TranslationsOrderDatabaseMapper
) : TranslationsOrderRepository {

    private val translationsOrderUpdates = BroadcastChannel<Unit>(1)

    override suspend fun getTranslationsOrder(): List<TranslationOrder> = withContext(Dispatchers.IO) {
        val orderDbModels = translationsOrderDao.getTranslationsOrder()
        translationsOrderDatabaseMapper.mapFromDatabase(orderDbModels)
    }

    override suspend fun saveTranslationsOrder(translationsOrder: List<TranslationOrder>) = withContext(Dispatchers.IO) {
        translationsOrderDao.deleteAllRecords()
        val translationOrderModels = translationsOrderDatabaseMapper.mapToDatabase(translationsOrder)
        translationsOrderDao.saveTranslationsOrder(translationOrderModels)
        translationsOrderUpdates.send(Unit)
    }

    override fun getTranslationOrderUpdates(): Flow<Unit> = translationsOrderUpdates.asFlow()

}