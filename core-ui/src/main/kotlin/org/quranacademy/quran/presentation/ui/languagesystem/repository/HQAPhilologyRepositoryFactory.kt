package org.quranacademy.quran.presentation.ui.languagesystem.repository

import org.quranacademy.quran.presentation.ui.languagesystem.fallback.FallbacksTree

object HQAPhilologyRepositoryFactory {


    fun getPhilologyRepository(languageCode: String): PhilologyRepository {
        if (languageCode == FallbacksTree.DEFAULT_LANGUAGE) {
            return EmptyPhilologyRepository
        }

        val isLanguageSupported = FallbacksTree.isLanguageSupported(languageCode)
        return if (isLanguageSupported) {
            CustomLanguagePhilologyRepository(languageCode, getFallbackRepository(languageCode))
        } else {
            getFallbackRepository(languageCode)
        }
    }

    private fun getFallbackRepository(languageCode: String): PhilologyRepository {
        val fallbackCode = FallbacksTree.getFallbackFor(languageCode)
        return if (fallbackCode != null) {
            val fallbackRepository = getFallbackRepository(fallbackCode)
            CustomLanguagePhilologyRepository(fallbackCode, fallbackRepository)
        } else {
            EmptyPhilologyRepository
        }
    }

}


