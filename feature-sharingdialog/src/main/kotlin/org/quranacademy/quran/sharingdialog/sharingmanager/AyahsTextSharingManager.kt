package org.quranacademy.quran.sharingdialog.sharingmanager

import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.TextNode
import org.quranacademy.quran.domain.commons.ResourcesManager
import org.quranacademy.quran.domain.models.*
import org.quranacademy.quran.domain.repositories.AyahsRepository
import org.quranacademy.quran.domain.repositories.SurahsRepository
import org.quranacademy.quran.presentation.ui.global.toArabicNumberIfNeeded
import org.quranacademy.quran.sharingdialog.R
import java.text.NumberFormat
import java.util.*
import javax.inject.Inject

class AyahsTextSharingManager @Inject constructor(
        private val resourcesManager: ResourcesManager,
        private val ayahsRepository: AyahsRepository,
        private val surahsRepository: SurahsRepository,
        private val ligatureTextProvider: org.quranacademy.quran.sharingdialog.sharingmanager.LigatureTextProvider
) {

    private val arabicNumberFormat = NumberFormat.getInstance(Locale("ar", "EG"))

    suspend fun getAyahsTextForSharing(
            selectedAyahs: List<AyahId>,
            selectedTranslations: List<Translation>,
            copyQuranArabicText: Boolean
    ): String {
        return selectedAyahs
                .map { ayahsRepository.getAyahDetails(it) }
                .groupBy { it.surahNumber }
                .map {
                    val surah = surahsRepository.getSurahDetails(it.key)
                    generateTextForSurah(
                            surah = surah,
                            ayahsDetailsList = it.value,
                            selectedTranslations = selectedTranslations,
                            copyQuranArabicText = copyQuranArabicText
                    )
                }.joinToString("\n\n")
    }

    private fun generateTextForSurah(
            surah: SurahDetails,
            ayahsDetailsList: List<AyahDetails>,
            selectedTranslations: List<Translation>,
            copyQuranArabicText: Boolean
    ): String {
        val textBuilder = StringBuilder()

        val arabicTexts = mutableListOf<String>()
        if (copyQuranArabicText) {
            ayahsDetailsList.forEach {
                arabicTexts.add("${arabicNumberFormat.format(it.ayahNumber)}. ${it.arabicText}")
            }
            val arabicText = arabicTexts.joinToString("\n")
            textBuilder.append(arabicText).append("\n\n")
        }

        val translationTextsMap = linkedMapOf<Translation, MutableList<String>>()
        ayahsDetailsList
                .forEach { ayahDetails ->
                    ayahDetails.translations.forEach { ayahTranslation ->
                        val translation = ayahTranslation.translation
                        translationTextsMap.getOrPut(translation) { mutableListOf() }
                                .add("${ayahDetails.ayahNumber}. ${getAyahText(ayahTranslation)}")
                    }
                }

        val translationsText = translationTextsMap
                .filter { return@filter selectedTranslations.contains(it.key) }
                .map {
                    val translation = it.key
                    val texts = it.value
                    return@map translation.name + "\n\n" + texts.joinToString("\n")
                }
                .joinToString("\n\n")
        textBuilder.append(translationsText)

        val first = ayahsDetailsList.first().ayahNumber.toArabicNumberIfNeeded()
        val last = ayahsDetailsList.last().ayahNumber.toArabicNumberIfNeeded()
        val ayahs = if (first == last) first else "$first - $last"
        val surahName = resourcesManager.getString(
                R.string.surah_name_sharing_template,
                surah.transliteratedName,
                ayahs
        )
        textBuilder
                .append("\n\n")
                .append(surahName)

        return textBuilder.toString().trim()
    }

    private fun getAyahText(ayahTranslation: AyahTranslation): String {
        return if (ayahTranslation.textHQAFormat != null) {
            simplifyText(ayahTranslation.textHQAFormat!!).trim()
        } else {
            ayahTranslation.simpleText!!.trim()
        }
    }

    private fun simplifyText(textHQAFormat: AyahTranslation.HQATextFormat): String {
        val ayahTextDocument = Jsoup.parse(textHQAFormat.text)
        replaceLigatures(ayahTextDocument)
        val footnotesText = replaceFootnotes(ayahTextDocument, textHQAFormat)
        val simplifiedTranslationText = ayahTextDocument.replaceBrWithNewLines()
        return "$simplifiedTranslationText\n\n$footnotesText"
    }

    private fun replaceLigatures(ayahTextDocument: Document) {
        ayahTextDocument.select("sas,as,radu,rada,radum,raduma")
                .forEach {
                    val ligatureText = ligatureTextProvider.getLigatureTextReplacement(it.tagName())
                    it.replaceWith(TextNode("($ligatureText)"))
                }
    }

    private fun replaceFootnotes(ayahTextDocument: Document, textHQAFormat: AyahTranslation.HQATextFormat): String {
        val footnotesTextBuilder = StringBuilder()
        ayahTextDocument.select("footnote")
                .forEach {
                    val footnoteId = it.attr("id")
                    it.replaceWith(TextNode("[$footnoteId]"))
                    val footnoteText = textHQAFormat.getFootnoteText(footnoteId)
                    val simplifiedFootnoteText = Jsoup.parse(footnoteText).replaceBrWithNewLines()
                    footnotesTextBuilder.append("\n\n").append("[$footnoteId] $simplifiedFootnoteText")
                }
        return footnotesTextBuilder.toString().trim()
    }

    private fun Document.replaceBrWithNewLines(): String = select("p")
            .joinToString("\n\n") { it.text().trim() }

}