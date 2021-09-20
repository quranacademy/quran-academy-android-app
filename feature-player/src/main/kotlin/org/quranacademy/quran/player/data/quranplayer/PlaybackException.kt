package org.quranacademy.quran.player.data.quranplayer

import org.quranacademy.quran.domain.models.AyahAudio
import org.quranacademy.quran.recitationsrepository.downloading.AudioDownloadException

sealed class PlaybackException(message: String, cause: Throwable? = null) : RuntimeException(message, cause) {

    class Downloading(
            ayahAudio: AyahAudio? = null,
            cause: AudioDownloadException? = null
    ) : PlaybackException("Ayah downloading error: $ayahAudio", cause)

    class Unknown(
            ayahAudio: AyahAudio? = null,
            message: String? = null
    ) : PlaybackException("Unknown playback error: $ayahAudio. Message:\n\n$message")

}