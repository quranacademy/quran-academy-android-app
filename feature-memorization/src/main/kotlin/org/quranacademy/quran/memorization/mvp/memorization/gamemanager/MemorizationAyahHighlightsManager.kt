package org.quranacademy.quran.memorization.mvp.memorization.gamemanager

import org.quranacademy.quran.domain.models.AyahId
import org.quranacademy.quran.mushaf.presentation.mvp.mushafpage.AyahHighlightType
import java.util.*
import javax.inject.Inject

class MemorizationAyahHighlightsManager @Inject constructor() {

    private val currentHighlights: SortedMap<AyahHighlightType, MutableSet<AyahId>> = TreeMap()

    fun hideAllAyahs(ayahs: List<AyahId>) {
        currentHighlights[AyahHighlightType.HIDDEN] = ayahs.toMutableSet()
    }

    fun showAyah(ayah: AyahId) {
        currentHighlights[AyahHighlightType.HIDDEN]?.remove(ayah)
    }

    fun highlightCurrentAyah(ayah: AyahId) {
        currentHighlights[AyahHighlightType.AUDIO] = mutableSetOf(ayah)
    }

    fun getHighlights() = TreeMap(currentHighlights)

}