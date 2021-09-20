package org.quranacademy.quran.presentation.ui.languagesystem.plurals

class LanguageRules(
        val languageCode: String,
        private val rules: PluralRulesSet
) {

    fun getPluralForNumber(number: Int): Plural {
        return rules.getPluralForNumber(number) ?: Plural.OTHER
    }

}