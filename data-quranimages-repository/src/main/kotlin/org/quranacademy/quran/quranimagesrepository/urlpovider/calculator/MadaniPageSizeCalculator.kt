package org.quranacademy.quran.quranimagesrepository.urlpovider.calculator

import org.quranacademy.quran.quranimagesrepository.DisplaySize
import javax.inject.Inject

class MadaniPageSizeCalculator @Inject constructor(
        displaySize: DisplaySize
) : PageSizeCalculator(displaySize) {

    override fun getWidthParameter(): Int {
        return when {
            maxWidth <= 320 -> 320
            maxWidth <= 480 -> 480
            maxWidth <= 800 -> 800
            maxWidth <= 1280 -> 1024
            else -> 1260
        }
    }

}