package org.quranacademy.quran.domain.commons

import org.joda.time.DateTime
import javax.inject.Inject

class Clock @Inject constructor() {

    fun now(): DateTime {
        return DateTime.now()
    }

    fun nowMillis(): Long {
        return System.currentTimeMillis()
    }

}
