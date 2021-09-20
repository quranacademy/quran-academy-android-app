package org.quranacademy.quran.mushafpageboundsrepository

import org.quranacademy.quran.data.database.daos.PageBoundsDao
import org.quranacademy.quran.data.mushaf.MushafPageBoundsDataSource
import org.quranacademy.quran.domain.models.PageBounds
import org.quranacademy.quran.mushafpageboundsrepository.database.PageBoundsDatabaseMapper
import javax.inject.Inject

class MushafPageBoundsDataSourceImpl @Inject constructor(
        private val pageBoundsDao: PageBoundsDao,
        private val pageBoundsDatabaseMapper: PageBoundsDatabaseMapper
) : MushafPageBoundsDataSource {

    override fun getPageBounds(pageNumber: Int): PageBounds {
        val pageBoundsModel = pageBoundsDao.getPageBounds(pageNumber)
        val ayahBoundsModels = pageBoundsDao.getPageAyahsBounds(pageNumber)
        val ayahMarkerBounds = pageBoundsDao.getAyahMarkerBounds(pageNumber)
        val surahHeaderBounds = pageBoundsDao.getSurahHeaderBounds(pageNumber)
        return pageBoundsDatabaseMapper.mapFromDatabase(
                pageBoundsModel = pageBoundsModel,
                pageAyahBounds = ayahBoundsModels,
                ayahMarkerPositions = ayahMarkerBounds,
                surahHeaderBounds = surahHeaderBounds
        )
    }

}