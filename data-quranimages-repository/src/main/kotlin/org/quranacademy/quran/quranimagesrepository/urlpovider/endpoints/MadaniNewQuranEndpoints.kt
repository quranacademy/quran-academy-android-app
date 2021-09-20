package org.quranacademy.quran.quranimagesrepository.urlpovider.endpoints

import org.quranacademy.quran.data.mushaf.ImageFilePathProvider
import org.quranacademy.quran.quranimagesrepository.urlpovider.calculator.MadaniNewPageSizeCalculator
import javax.inject.Inject

class MadaniNewQuranEndpoints @Inject constructor(
        private val imageFilePathProvider: ImageFilePathProvider,
        calculator: MadaniNewPageSizeCalculator
) : QuranEndpoints(MADANI_NEW_ENDPOINT, calculator) {

    companion object {
        const val MADANI_NEW_ENDPOINT = "https://quranacademy.org/mobile-app/quran/mushaf"
    }

    override fun getMushafBundleUrl(): String {
        return "$endpoint/images/$screenSize.zip"
    }

    override fun getMushafImageUrl(pageNumber: Int): String {
        val pageName = imageFilePathProvider.getPageName(pageNumber)
        return "$endpoint/images/$screenSize/${pageName}"
    }

    override fun getMushafAyahBoundsUrl(): String {
        return "$endpoint/ayah-info/$screenSize.zip"
    }

}