package org.quranacademy.quran.domain.exceptions

import java.io.IOException

class NoNetworkException(
        message: String? = null,
        error: Throwable? = null
) : IOException(message, error)