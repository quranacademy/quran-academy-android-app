package org.quranacademy.quran.player.data.quranplayer.playbackcontrollers.audiofocus.audiofocusmanager

data class AudioFocusRequestCompat(
        val audioAttributes: AudioAttributesCompat,
        val acceptsDelayedFocusGain: Boolean,
        val onAudioFocusChangeListener: (Int) -> Unit,
        val willPauseWhenDucked: Boolean
)