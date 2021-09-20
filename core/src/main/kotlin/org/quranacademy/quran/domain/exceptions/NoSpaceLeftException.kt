package org.quranacademy.quran.domain.exceptions

class NoSpaceLeftException(message: String? = null, error: Throwable? = null) : RuntimeException(message, error)