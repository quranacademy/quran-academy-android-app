package org.quranacademy.quran.domain.exceptions

class ServerError(val errorCode: Int) : RuntimeException()