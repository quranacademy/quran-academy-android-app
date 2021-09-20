package org.quranacademy.quran.surahdetails.mvp

import org.quranacademy.quran.domain.models.AyahTranslation
import org.quranacademy.quran.domain.models.AyahWordItem

class AyahUiModel(
        val surahNumber: Int,
        val ayahNumber: Int,
        val isSajdaAyah: Boolean,
        val showPageAyah: Boolean,
        val pageNumber: Int,
        val juzNumber: Int,
        val translations: List<AyahTranslation>,
        val words: List<AyahWordItem>,
        val isWordByWordEnabled: Boolean
)