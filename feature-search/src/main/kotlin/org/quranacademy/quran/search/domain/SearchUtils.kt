package org.quranacademy.quran.search.domain

object SearchUtils {

    fun doesStringContainArabic(s: String): Boolean {
        val length = s.length
        for (i in 0 until length) {
            val current = s[i].toInt()
            // Skip space
            if (current == 32) {
                continue
            }
            // non-reshaped arabic
            if (current in 1570..1610) {
                return true
            } else if (current in 65133..65276) {
                return true
            } else if (current != 42 && current != 40 && current != 41 && current != 91 && current != 93) {
                return false
            }

            // if the value is 42, it deserves another chance :p
            // (in reality, 42 is a * which is useful in searching sqlite)
            // also whitelist () and []
            // re-shaped arabic
        }
        return false
    }

}