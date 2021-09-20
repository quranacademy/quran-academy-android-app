package org.quranacademy.quran.presentation.ui.languagesystem.plurals.rules

import org.quranacademy.quran.presentation.ui.languagesystem.plurals.Plural
import org.quranacademy.quran.presentation.ui.languagesystem.plurals.PluralRule
import org.quranacademy.quran.presentation.ui.languagesystem.plurals.PluralRulesSet

class PluralRulesSet29 : PluralRulesSet() {

    override val rules: List<PluralRule> = listOf(
            PluralRule(Plural.FEW) { it % 10 in 2..4 && it % 100 !in 12..14 },
            PluralRule(Plural.MANY) { it % 10 == 0 || it % 10 in 5..9 || it % 100 in 11..14 },
            PluralRule(Plural.ONE) { it % 10 == 1 && it % 100 != 11 }
    )

}