package org.quranacademy.quran.translationsorder

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.quranacademy.quran.data.database.adapters.TranslationDatabaseManager
import org.quranacademy.quran.data.database.adapters.TranslationsOrderManager
import org.quranacademy.quran.data.database.daos.TranslationsOrderDao
import org.quranacademy.quran.data.database.models.TranslationOrderModel
import org.quranacademy.quran.domain.repositories.TranslationsOrderRepository
import javax.inject.Inject

class TranslationsOrderManagerImpl @Inject constructor(
        private val translationsOrderDao: TranslationsOrderDao,
        private val translationsOrderRepository: TranslationsOrderRepository,
        private val translationDatabaseManager: TranslationDatabaseManager
) : TranslationsOrderManager {

    private var translationsOrder: LinkedHashMap<String, TranslationOrderModel> = loadTranslationsOrder()

    init {
        GlobalScope.launch {
            translationsOrderRepository.getTranslationOrderUpdates().collect {
                translationsOrder = loadTranslationsOrder()
            }
        }
    }

    /**
     * При обновлении списка переводов создает новые модели в таблице TranslationsOrder
     */
    override suspend fun onTranslationsListUpdated() = withContext(Dispatchers.IO) {
        val oldTranslationsOrder = translationsOrderDao.getTranslationsOrder()
        val translationsOrderCodes = oldTranslationsOrder.map { it.translationCode }

        val enabledTranslations = translationDatabaseManager.getAdapters().map { it.getTranslation() }
        val enabledTranslationsCodes = enabledTranslations.map { it.code }

        //There may be two situations: a translation is enabled or disabled
        //1. If the translation is enabled, we have to add new item in the end of thr "Translations order" list
        //2. If the translation is disabled, we have to remove an item, that associated with disabled translation

        //remove disabled transnlations
        val newTranslationsOrder = oldTranslationsOrder
                .filter { enabledTranslationsCodes.contains(it.translationCode) }
                .mapIndexed { index, translationOrderModel ->
                    TranslationOrderModel(
                            translationCode = translationOrderModel.translationCode,
                            showInDialog = translationOrderModel.showInDialog,
                            order = index
                    )
                }
                .toMutableList()

        val newEnabledTranslation: TranslationOrderModel? = enabledTranslations
                .firstOrNull { !translationsOrderCodes.contains(it.code) }
                ?.let { translation ->
                    TranslationOrderModel(
                            translationCode = translation.code,
                            showInDialog = translation.isTafseer,
                            order = newTranslationsOrder.size //last item index
                    )
                }

        //if new enabled item is found, then add it in the end of orders list
        if (newEnabledTranslation != null) {
            newTranslationsOrder.add(newEnabledTranslation)
        }

        translationsOrderDao.deleteAllRecords()
        translationsOrderDao.saveTranslationsOrder(newTranslationsOrder)
        translationsOrder = loadTranslationsOrder()
    }

    override fun getTranslationsOrder(): LinkedHashMap<String, TranslationOrderModel> = translationsOrder

    private fun loadTranslationsOrder(): LinkedHashMap<String, TranslationOrderModel> {
        val translationsOrder = translationsOrderDao.getTranslationsOrder()
                .map { it.translationCode to it }
        return linkedMapOf(*translationsOrder.toTypedArray())
    }

}