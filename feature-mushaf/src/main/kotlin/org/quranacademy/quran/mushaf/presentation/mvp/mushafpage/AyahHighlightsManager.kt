package org.quranacademy.quran.mushaf.presentation.mvp.mushafpage

import org.quranacademy.quran.domain.models.AyahId
import java.util.*

class AyahHighlightsManager {

    val currentHighlights: SortedMap<AyahHighlightType, MutableSet<AyahId>> = TreeMap()

    fun highlightAyahs(ayahs: List<AyahId>, highlightType: AyahHighlightType, addToOld: Boolean) {
        val ayahHighlightsForType = currentHighlights.getOrPut(highlightType) { mutableSetOf() }
        if (!addToOld) {
            ayahHighlightsForType.clear()
        }
        ayahHighlightsForType.addAll(ayahs)
    }

    fun unhighlightAyahsWithType(ayahs: List<AyahId>, highlightType: AyahHighlightType) {
        currentHighlights.getOrPut(highlightType) { mutableSetOf() }?.removeAll(ayahs)
    }

}