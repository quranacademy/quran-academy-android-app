package org.quranacademy.quran.wordbywordrepository

import org.quranacademy.quran.domain.models.AyahWord
import org.quranacademy.quran.domain.models.AyahWordGroup
import org.quranacademy.quran.domain.models.AyahWordItem
import javax.inject.Inject

class WordByWordGrouper @Inject constructor() {

    /**
     * Группирует слова.
     *
     * Слова, у которых в поле "translationText" указана звездочка, присоединяется
     * к предыдущему слову, образуя группу.
     *
     */
    fun groupWords(words: List<AyahWord>): List<AyahWordItem> {
        val ayahItems = mutableListOf<AyahWordItem>()
        val lastItemIndex = words.size - 1
        var lastGroupItem: AyahWordGroup? = null

        for (index in words.indices) {
            val currentElement = words[index]
            if (currentElement.translationText == "*") {
                lastGroupItem?.words!!.add(currentElement)
                continue
            }

            val isLast = index == lastItemIndex
            if (!isLast) {
                val nextItem = words[index + 1]
                if (nextItem.translationText == "*") {
                    lastGroupItem = AyahWordGroup(currentElement.translationText!!)
                    lastGroupItem.words.add(currentElement)
                    ayahItems.add(lastGroupItem)
                    continue
                }
            }

            ayahItems.add(currentElement)
        }

        return ayahItems
    }

}