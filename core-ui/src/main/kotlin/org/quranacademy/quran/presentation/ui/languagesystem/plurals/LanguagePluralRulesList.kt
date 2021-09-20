package org.quranacademy.quran.presentation.ui.languagesystem.plurals

import org.quranacademy.quran.presentation.ui.languagesystem.plurals.rules.*

/*
    Rules from Android:

    https://github.com/LineageOS/android_external_icu/blob/lineage-16.0/icu4c/source/data/misc/plurals.txt
    https://www.unicode.org/cldr/charts/34/supplemental/language_plural_rules.html

    More info: http://cldr.unicode.org/index/cldr-spec/plural-rules

 */
object LanguagePluralRulesList {

    private val languageRules: Map<String, LanguageRules>

    init {
        val pluralRulesSet0 = PluralRulesSet0()
        val pluralRulesSet4 = PluralRulesSet4()
        val pluralRulesSet8 = PluralRulesSet8()
        val pluralRulesSet29 = PluralRulesSet29()

        languageRules = listOf(
                LanguageRules("az", pluralRulesSet8),
                LanguageRules("av", pluralRulesSet4),
                LanguageRules("ar", PluralRulesSet33()),
                LanguageRules("ba", pluralRulesSet0),
                LanguageRules("ce", pluralRulesSet4),
                LanguageRules("de", pluralRulesSet4),
                LanguageRules("es", pluralRulesSet8),
                LanguageRules("fr", PluralRulesSet2()),
                LanguageRules("inh", pluralRulesSet4),
                LanguageRules("kk", pluralRulesSet8),
                LanguageRules("lez", pluralRulesSet0),
                LanguageRules("nl", pluralRulesSet4),
                LanguageRules("ru", pluralRulesSet29),
                LanguageRules("tr", pluralRulesSet8),
                LanguageRules("tt", pluralRulesSet0),
                LanguageRules("uk", pluralRulesSet29)
        ).map { it.languageCode to it }.toMap()
    }

    fun getRulesForLanguage(languageCode: String): LanguageRules {
        return languageRules[languageCode]
                ?: throw IllegalArgumentException("Plural rules for language with code \"$languageCode\" not found")
    }

}