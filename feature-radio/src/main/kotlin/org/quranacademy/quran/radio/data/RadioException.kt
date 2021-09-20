package org.quranacademy.quran.radio.data

sealed class RadioException(
        message: String, cause: Throwable? = null
) : RuntimeException(message, cause) {

    class Network(
            message: String = "Network Error",
            cause: Exception? = null
    ) : RadioException(message, cause)

}