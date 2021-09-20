package org.quranacademy.quran.quranimagesrepository.urlpovider.calculator

import org.quranacademy.quran.quranimagesrepository.DisplaySize

abstract class PageSizeCalculator(displaySize: DisplaySize) {

    protected val maxWidth: Int = if (displaySize.x > displaySize.y) displaySize.x else displaySize.y

    abstract fun getWidthParameter(): Int

}