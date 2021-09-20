package org.quranacademy.quran.quranimagesrepository

import org.quranacademy.quran.QuranConstants
import org.quranacademy.quran.data.mushaf.ImageFilePathProvider
import org.quranacademy.quran.data.mushaf.QuranImageFilesChecker
import javax.inject.Inject

class QuranImageFilesCheckerImpl @Inject constructor(
        private val imageFilePathProvider: ImageFilePathProvider
) : QuranImageFilesChecker {

    override fun haveAllImages(): Boolean {
        for (pageNumber in 1..QuranConstants.QURAN_PAGES_COUNT) {
            if (!imageFilePathProvider.getPageImageFile(pageNumber).exists()) {
                return false
            }
        }
        return true
    }

}