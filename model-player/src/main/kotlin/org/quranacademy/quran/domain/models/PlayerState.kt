package org.quranacademy.quran.domain.models

import java.io.Serializable

enum class PlayerState : Serializable {

    IDLE, DOWNLOADING, LOADING, PLAYING, PAUSED, ERROR

}



