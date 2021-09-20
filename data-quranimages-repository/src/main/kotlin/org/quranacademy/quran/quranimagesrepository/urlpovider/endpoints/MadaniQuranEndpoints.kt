package org.quranacademy.quran.quranimagesrepository.urlpovider.endpoints

import org.quranacademy.quran.data.mushaf.ImageFilePathProvider
import org.quranacademy.quran.quranimagesrepository.urlpovider.calculator.MadaniPageSizeCalculator
import javax.inject.Inject

class MadaniQuranEndpoints @Inject constructor(
        private val imageFilePathProvider: ImageFilePathProvider,
        calculator: MadaniPageSizeCalculator
) : QuranEndpoints(MADANI_ENDPOINT, calculator) {

    companion object {
        const val MADANI_ENDPOINT = "https://quranacademy.org/mobile-app/quran/android/mushaf"
    }

    override fun getMushafBundleUrl(): String {
        return "$endpoint/images-archives/$screenSize.zip"
    }

    override fun getMushafImageUrl(pageNumber: Int): String {
        val pageName = imageFilePathProvider.getPageName(pageNumber)
        return "$endpoint/images/$screenSize/$pageName"
    }

    override fun getMushafAyahBoundsUrl(): String {
        return "$endpoint/images-info-databases/$screenSize.zip"
    }

}