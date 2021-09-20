package org.quranacademy.quran.tajweedrules.mvp

data class TajweedRule(
        val name: String,
        val description: String,
        val arabicTextSample: String,
        val color: String,
        val audioFileResId: Int
)