package org.quranacademy.quran.domain.models

data class ReadSettings(
        var isOpenLastReadingPlaceEnabled: Boolean,
        var isMushafMode: Boolean,
        var lastReadSurah: Int,
        var lastReadAyah: Int,
        var arabicFont: String?
)