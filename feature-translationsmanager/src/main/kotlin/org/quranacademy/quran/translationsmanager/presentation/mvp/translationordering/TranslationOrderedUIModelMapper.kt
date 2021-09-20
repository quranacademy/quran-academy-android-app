package org.quranacademy.quran.translationsmanager.presentation.mvp.translationordering

import org.quranacademy.quran.domain.models.Language
import org.quranacademy.quran.domain.models.Translation
import org.quranacademy.quran.domain.models.TranslationOrder
import javax.inject.Inject

class TranslationOrderedUIModelMapper @Inject constructor() {

    fun mapToUIModel(
            translationOrder: List<TranslationOrder>,
            translations: List<Translation>,
            languages: List<Language>
    ): List<TranslationOrderedUIModel> {
        val translationsMap = translations.associateBy { it.code }
        val languagesMap = languages.associateBy { it.code }
        return translationOrder.map {
            val translation = translationsMap[it.translationCode]!!
            val language = languagesMap[translation.languageCode]!!
            return@map TranslationOrderedUIModel(
                    translationCode = translation.code,
                    languageName = language.name,
                    name = translation.name,
                    showInDialog = it.showInDialog,
                    order = it.order
            )
        }
    }

}