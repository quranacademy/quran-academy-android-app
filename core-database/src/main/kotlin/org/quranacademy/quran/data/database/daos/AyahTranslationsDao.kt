package org.quranacademy.quran.data.database.daos

import org.quranacademy.quran.data.database.adapters.TranslationDatabaseAdapter
import org.quranacademy.quran.data.database.adapters.TranslationDatabaseManager
import org.quranacademy.quran.data.database.adapters.TranslationsOrderManager
import org.quranacademy.quran.data.database.models.AyahTranslationModel
import org.quranacademy.quran.domain.models.Translation
import javax.inject.Inject

class AyahTranslationsDao @Inject constructor(
        private val translationDatabaseManager: TranslationDatabaseManager,
        private val tanslationsOrderManager: TranslationsOrderManager
) {

    fun getTranslationsForShowingInList(surahNumber: Int): Map<Translation, List<AyahTranslationModel>> {
        return getOrderedTranslations(false)
                .map { it.getTranslation() to it.getTranslationsForSurah(surahNumber) }
                .toMap()
    }

    fun getTranslationsForShowingInDialog(surahNumber: Int, ayahNumber: Int): Map<Translation, AyahTranslationModel> {
        return getOrderedTranslations(true)
                .map { it.getTranslation() to it.getTranslationForAyah(surahNumber, ayahNumber) }
                .toMap()
    }

    fun getAllAyahTranslations(surahNumber: Int, ayahNumber: Int): Map<Translation, AyahTranslationModel> {
        val translationsOrder = tanslationsOrderManager.getTranslationsOrder()
        return translationDatabaseManager.getAdapters()
                .sortedBy { translationsOrder[it.getTranslation().code]!!.order }
                .map { it.getTranslation() to it.getTranslationForAyah(surahNumber, ayahNumber) }
                .toMap()
    }

    private fun getOrderedTranslations(showInDialog: Boolean): List<TranslationDatabaseAdapter> {
        val showInListTranslations = tanslationsOrderManager.getTranslationsOrder()
                .filter { showInDialog == it.value.showInDialog }
        return translationDatabaseManager.getAdapters()
                .filter { showInListTranslations.contains(it.getTranslation().code) }
                .sortedBy { showInListTranslations[it.getTranslation().code]!!.order }
    }

}