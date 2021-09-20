package org.quranacademy.quran.player.presentation.global.extensions

fun Long.formatDuration(): String {
    val seconds = this / 1000
    val absSeconds = Math.abs(seconds)
    val positive = String.format(
            "%02d:%02d",
            absSeconds % 3600 / 60,
            absSeconds % 60)
    return if (seconds < 0) "-$positive" else positive
}