package org.quranacademy.quran.presentation.ui.languagesystem.repository

import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.quranacademy.quran.presentation.ui.languagesystem.Philology
import org.quranacademy.quran.presentation.ui.languagesystem.plurals.LanguagePluralRulesList
import org.quranacademy.quran.presentation.ui.languagesystem.plurals.LanguageRules

class CustomLanguagePhilologyRepository(
        val languageCode: String,
        private val fallbackRepository: PhilologyRepository
) : PhilologyRepository {

    private val stringsDocument: Document by lazy { parseDocument() }
    private val pluralRules: LanguageRules = LanguagePluralRulesList.getRulesForLanguage(languageCode)
    private val words: HashMap<String, String> by lazy { parseStrings() }
    private val plurals: HashMap<String, HashMap<String, String>> by lazy { parsePlurals() }

    override fun getString(key: String): String? {
        return words[key] ?: fallbackRepository.getString(key)
    }

    override fun getQuantityString(key: String, quantity: Int): String? {
        val plural: Map<String, String> = plurals[key]
                ?: return fallbackRepository.getQuantityString(key, quantity)
        val quantityCode = pluralRules.getPluralForNumber(quantity).code
        return plural[quantityCode]
    }

    private fun parseDocument(): Document {
        val assetsTextFileReader = AssetsTextFileReader(Philology.getOriginalContext())
        val stringsContent = assetsTextFileReader.readFile("languages/$languageCode.xml")
        return Jsoup.parse(stringsContent)
    }

    private fun parseStrings(): HashMap<String, String> {
        val words = hashMapOf<String, String>()
        val stringNodes = stringsDocument.select("resources > string")
        stringNodes.forEach {
            val text = it.html().replace("\\n", "\n")
            words[it.attr("name")] = text
        }
        return words
    }

    private fun parsePlurals(): HashMap<String, HashMap<String, String>> {
        val plurals = hashMapOf<String, HashMap<String, String>>()
        val pluralsNodes = stringsDocument.select("resources > plurals")
        pluralsNodes.forEach { pluralNode ->
            val pluralItems = hashMapOf<String, String>()
            plurals[pluralNode.attr("name")] = pluralItems
            pluralNode.select("item").forEach { pluralItem ->
                pluralItems[pluralItem.attr("quantity")] = pluralItem.html()
            }
        }
        return plurals
    }

}
