package org.quranacademy.quran.presentation.ui.languagesystem.plurals

class PluralRule(
        val plural: Plural,
        private val isFulfilledChecker: (Int) -> Boolean
) {

    fun isFulfilled(number: Int): Boolean = isFulfilledChecker(number)

}