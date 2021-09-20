package org.quranacademy.quran.mushaf.presentation.mvp

import org.quranacademy.quran.domain.models.AyahId
import org.quranacademy.quran.mushaf.presentation.mvp.mushafpage.AyahHighlightType

class HighlightAyahsCommand(
        val pageNumber: Int,
        val selectedAyahs: List<AyahId>,
        val highlightType: AyahHighlightType,
        val isHighlighted: Boolean,
        val addToOld: Boolean
)