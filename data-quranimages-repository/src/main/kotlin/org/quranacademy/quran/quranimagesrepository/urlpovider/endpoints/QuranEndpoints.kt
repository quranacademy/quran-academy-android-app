package org.quranacademy.quran.quranimagesrepository.urlpovider.endpoints

import org.quranacademy.quran.quranimagesrepository.urlpovider.calculator.PageSizeCalculator

abstract class QuranEndpoints(
        protected val endpoint: String,
        private val calculator: PageSizeCalculator
) {

    protected val screenSize: Int
        get() = calculator.getWidthParameter()

    abstract fun getMushafBundleUrl(): String

    abstract fun getMushafImageUrl(pageNumber: Int): String

    abstract fun getMushafAyahBoundsUrl(): String

}