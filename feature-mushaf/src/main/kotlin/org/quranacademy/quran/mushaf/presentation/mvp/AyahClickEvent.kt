package org.quranacademy.quran.mushaf.presentation.mvp

import org.quranacademy.quran.domain.models.AyahId

class AyahClickEvent(
        val ayahId: AyahId,
        val clickType: ClickType,
        val pageNumber: Int
) {

    enum class ClickType {
        SINGLE, DOUBLE, LONG
    }

}