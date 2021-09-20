package org.quranacademy.quran.quranimagesrepository.urlpovider.calculator

import org.quranacademy.quran.quranimagesrepository.DisplaySize
import javax.inject.Inject

class MadaniNewPageSizeCalculator @Inject constructor(
        displaySize: DisplaySize
) : PageSizeCalculator(displaySize) {

    override fun getWidthParameter(): Int {
        return when {
            maxWidth <= 1280 -> 1120
            else -> 1260
        }
    }

}