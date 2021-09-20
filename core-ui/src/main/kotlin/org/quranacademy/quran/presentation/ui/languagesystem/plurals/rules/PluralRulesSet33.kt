package org.quranacademy.quran.presentation.ui.languagesystem.plurals.rules

import org.quranacademy.quran.presentation.ui.languagesystem.plurals.Plural
import org.quranacademy.quran.presentation.ui.languagesystem.plurals.PluralRule
import org.quranacademy.quran.presentation.ui.languagesystem.plurals.PluralRulesSet

class PluralRulesSet33 : PluralRulesSet() {

    override val rules: List<PluralRule> = listOf(
            PluralRule(Plural.FEW) { it % 100 in 3..10 },
            PluralRule(Plural.MANY) { it % 100 in 11..99 },
            PluralRule(Plural.ONE) { it == 1 },
            PluralRule(Plural.TWO) { it == 2 },
            PluralRule(Plural.ZERO) { it == 0 }
    )

}