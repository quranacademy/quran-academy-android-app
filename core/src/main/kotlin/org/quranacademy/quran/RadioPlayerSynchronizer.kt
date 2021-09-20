package org.quranacademy.quran

interface RadioPlayerSynchronizer {

    fun isPlayerActive(): Boolean

    fun isRadioActive(): Boolean

    fun stopPlayer(onStopped: () -> Unit)

    fun stopRadio(onStopped: () -> Unit)

}