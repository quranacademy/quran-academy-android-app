package org.quranacademy.quran.mainscreen.presentation.mvp.surahslist

import org.quranacademy.quran.domain.models.AyahId
import org.quranacademy.quran.domain.models.Surah

class QuranJuz(
        val number: Int,
        val startPageNumber: Int,
        val ayah: AyahId,
        val surahs: List<Surah>
)