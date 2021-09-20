package org.quranacademy.quran.player.presentation.playeroptions

import org.quranacademy.quran.domain.models.Recitation
import javax.inject.Inject

class PlayerOptionsShell @Inject constructor() {

    private lateinit var onRecitationSelectedListener: RecitationSelectListener

    fun onRecitationSelected(listener: RecitationSelectListener) {
        onRecitationSelectedListener = listener
    }

    fun onRecitationSelected(recitation: Recitation) {
        onRecitationSelectedListener(recitation)
    }

}

typealias RecitationSelectListener = (recitation: Recitation) -> Unit