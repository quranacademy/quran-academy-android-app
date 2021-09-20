package org.quranacademy.quran.data.mushaf

import org.quranacademy.quran.data.PathProvider
import org.quranacademy.quran.domain.models.MushafPageType
import java.io.File
import java.text.NumberFormat
import java.util.*
import javax.inject.Inject

class ImageFilePathProvider @Inject constructor(
        private val pathProvider: PathProvider
) {

    private val PAGE_NUMBER_FORMAT by lazy {
        val numberFormat = NumberFormat.getInstance(Locale.US)
        numberFormat.minimumIntegerDigits = 3
        return@lazy numberFormat
    }

    fun getPageImageFile(pageNumber: Int, pageType: MushafPageType? = null): File {
        val imagesFolder = if (pageType != null) {
            pathProvider.getImagesFolderForType(pageType)
        } else {
            pathProvider.imagesFolder
        }
        return File(imagesFolder, getPageName(pageNumber))
    }


    fun getPageName(pageNumber: Int) = "page${PAGE_NUMBER_FORMAT.format(pageNumber)}.png"

}