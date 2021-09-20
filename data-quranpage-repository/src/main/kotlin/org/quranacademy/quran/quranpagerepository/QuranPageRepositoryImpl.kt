package org.quranacademy.quran.quranpagerepository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.quranacademy.quran.QuranConstants
import org.quranacademy.quran.data.database.daos.PageBookmarksDao
import org.quranacademy.quran.data.database.daos.SurahsNameTranslationsDao
import org.quranacademy.quran.data.mushaf.ImageFilePathProvider
import org.quranacademy.quran.data.mushaf.MushafPageBoundsDataSource
import org.quranacademy.quran.domain.models.QuranPage
import org.quranacademy.quran.domain.repositories.QuranPageRepository
import javax.inject.Inject

class QuranPageRepositoryImpl @Inject constructor(
        private val pageBookmarksDao: PageBookmarksDao,
        private val surahsNameTranslationsDao: SurahsNameTranslationsDao,
        private val imageFilePathProvider: ImageFilePathProvider,
        private val pageBoundsDataSource: MushafPageBoundsDataSource
) : QuranPageRepository {

    override suspend fun getPageInfo(pageNumber: Int): QuranPage = withContext(Dispatchers.IO) {
        val pagePosition = pageNumber - 1
        val surahNumber = QuranConstants.SURAH_FOR_PAGE[pagePosition]
        val pageFirstAyahNumber = QuranConstants.AYAH_FOR_PAGE[pagePosition]
        val imageFile = imageFilePathProvider.getPageImageFile(pageNumber)
        val isImageExists = imageFile.exists()
        val surahNameTranslation = surahsNameTranslationsDao.getSurahNameByNumber(surahNumber)
        val isPageBookmarked = pageBookmarksDao.isPageBookmarked(pageNumber)
        val pageAyahsBounds = pageBoundsDataSource.getPageBounds(pageNumber)
        val juzNumber = QuranConstants.getPageJuz(pageNumber)

        QuranPage(
                pageNumber = pageNumber,
                imagePath = imageFile.absolutePath,
                isImageExists = isImageExists,
                firstAyahNumber = pageFirstAyahNumber,
                surahNumber = surahNumber,
                juzNumber = juzNumber,
                rubNumber = 0,
                surahName = surahNameTranslation.transliteratedName,
                surahTranslatedName = surahNameTranslation.translatedName,
                isBookmarked = isPageBookmarked,
                pageBounds = pageAyahsBounds
        )
    }

}