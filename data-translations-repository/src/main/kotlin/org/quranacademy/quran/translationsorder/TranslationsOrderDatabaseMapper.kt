package org.quranacademy.quran.translationsorder

import org.quranacademy.quran.data.database.models.TranslationOrderModel
import org.quranacademy.quran.domain.models.TranslationOrder
import javax.inject.Inject

class TranslationsOrderDatabaseMapper @Inject constructor() {

    fun mapFromDatabase(
            translations: List<TranslationOrderModel>
    ): List<TranslationOrder> {
        return translations.map {
            TranslationOrder(
                    translationCode = it.translationCode,
                    showInDialog = it.showInDialog,
                    order = it.order
            )
        }
    }

    fun mapToDatabase(translationsOrder: List<TranslationOrder>): List<TranslationOrderModel> {
        return translationsOrder.map {
            TranslationOrderModel(
                    translationCode = it.translationCode,
                    showInDialog = it.showInDialog,
                    order = it.order
            )
        }
    }

}

