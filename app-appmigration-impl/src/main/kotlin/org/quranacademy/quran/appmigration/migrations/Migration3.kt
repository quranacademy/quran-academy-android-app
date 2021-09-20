package org.quranacademy.quran.appmigration.migrations

import org.quranacademy.quran.appmigration.AppMigration
import org.quranacademy.quran.data.database.daos.TranslationsDao
import org.quranacademy.quran.data.database.daos.TranslationsOrderDao
import org.quranacademy.quran.data.database.models.TranslationOrderModel
import javax.inject.Inject

class Migration3 @Inject constructor(
        private val translationsrDao: TranslationsDao,
        private val translationsOrderDao: TranslationsOrderDao
) : AppMigration(3) {

    override fun apply() {
        configTranslationsOrder()
    }

    private fun configTranslationsOrder() {
        val downloadedTranslations = translationsrDao.getAllTranslations().filter { it.isEnabled }
        val translationsOrder = downloadedTranslations.mapIndexed { index, translation ->
            TranslationOrderModel(
                    translationCode = translation.code,
                    showInDialog = translation.isTafseer,
                    order = index
            )
        }
        translationsOrderDao.saveTranslationsOrder(translationsOrder)
    }


}