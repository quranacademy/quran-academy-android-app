package org.quranacademy.quran.presentation.ui.languagesystem.plurals

abstract class PluralRulesSet {

    abstract val rules: List<PluralRule>

    fun getPluralForNumber(number: Int): Plural? {
        return rules.firstOrNull { it.isFulfilled(number) }?.plural
    }

}