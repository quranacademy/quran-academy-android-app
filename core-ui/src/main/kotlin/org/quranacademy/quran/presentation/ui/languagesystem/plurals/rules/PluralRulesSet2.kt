package org.quranacademy.quran.presentation.ui.languagesystem.plurals.rules

import org.quranacademy.quran.presentation.ui.languagesystem.plurals.Plural
import org.quranacademy.quran.presentation.ui.languagesystem.plurals.PluralRule
import org.quranacademy.quran.presentation.ui.languagesystem.plurals.PluralRulesSet

class PluralRulesSet2 : PluralRulesSet() {

    override val rules: List<PluralRule> = listOf(
            PluralRule(Plural.ONE) { it == 0 || it == 1 }
    )

}