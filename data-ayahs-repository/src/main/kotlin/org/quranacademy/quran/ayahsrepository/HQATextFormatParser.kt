package org.quranacademy.quran.ayahsrepository

import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import org.jsoup.nodes.Node
import org.quranacademy.quran.Constants
import org.quranacademy.quran.domain.models.AyahTranslation
import javax.inject.Inject

class HQATextFormatParser @Inject constructor() {

    fun parse(text: String): AyahTranslation.HQATextFormat {
        val document = Jsoup.parse(text)
        val translationTextNode = document.select("content > text").first()
        correctArabicTextAlign(translationTextNode)
        val translationText = translationTextNode.html()

        val footnoteContentNodes = document.select("content > footnotes > footnote-content")
        val footnotes = footnoteContentNodes.map { footnote ->
            val footnoteId = footnote.attr("id")
            val footnoteText = footnote.html()
            footnoteId to footnoteText
        }.toMap()
        return AyahTranslation.HQATextFormat(
                text = translationText,
                footnotes = footnotes
        )
    }

    // текст перевода разбит на параграфы. Если параграф содержит русский текст, но начинается с арабского текста,
    // возникают проблемы с выравниванием текста. Текст в таком параграфе прижимается вправо, и выглядит это все очень некрасиво
    // Для решения этой проблемы в начале таких параграфов ставится спец. знак с кодом ("\u2800").
    // В тексте он выглядит как длинный пробел. Он позволяет прижать текст влево. Чтобы определить,
    // нужно ли вставлять в данный параграф указанный символ, мы проверяем, начинается ли его содержимое с тега "arabic"
    // Тег arabic может как непосредственно внутри параграфа, так и внутри тегов "hadith-translation"
    // и "quran-translation".
    private fun correctArabicTextAlign(text: Element) {
        val paragraphs = text.select("text > p")
        paragraphs.forEach { paragraph ->
            val childNodes = paragraph.childNodes()
            if (childNodes.size >= 1) {
                val firstParagraphArabicNode = getFirstArabicNode(paragraph)
                if (firstParagraphArabicNode != null) {
                    fixArabicTextAlign(firstParagraphArabicNode)
                } else {
                    val translationNode = paragraph.select("hadith-translation, quran-translation").firstOrNull()
                    if (translationNode != null) {
                        val firstTranslationArabicNode = getFirstArabicNode(translationNode)
                        firstTranslationArabicNode?.let { fixArabicTextAlign(it) }
                    }
                }

            }
        }
    }

    // Данный метод ищет тег arabic в качестве первого элемента параграфа
    // Если первый элемент является тегом "arabic" (что означает, что параграф
    // начинавется с арабского текста), то возвращаем его, если нет, то null
    private fun getFirstArabicNode(paragraph: Node): Element? {
        if (paragraph.childNodeSize() == 0) {
            return null
        }

        val firstChild = paragraph.childNode(0)
        val firstChildTagName = firstChild.nodeName()
        return if (firstChildTagName == "arabic") {
            firstChild as Element
        } else {
            null
        }
    }

    private fun fixArabicTextAlign(element: Element) {
        element.text(Constants.RTL_FIXER_SYMBOL + element.text())
    }

}