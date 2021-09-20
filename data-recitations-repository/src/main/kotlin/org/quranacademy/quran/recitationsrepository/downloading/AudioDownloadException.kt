package org.quranacademy.quran.recitationsrepository.downloading

abstract class AudioDownloadException(
        message: String? = null, cause: Throwable? = null
) : RuntimeException(message, cause) {

    class IncorrectAudioUrl(message: String? = null, cause: Throwable? = null) : AudioDownloadException(message, cause)

    class NoNetwork(message: String? = null, cause: Throwable? = null) : AudioDownloadException(message, cause)

    class Unknown(message: String? = null, cause: Throwable? = null) : AudioDownloadException(message, cause)

}