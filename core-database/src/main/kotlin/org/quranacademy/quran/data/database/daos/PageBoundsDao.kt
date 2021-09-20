package org.quranacademy.quran.data.database.daos

import org.quranacademy.quran.data.database.adapters.MushafPageBoundsDatabaseAdapter
import org.quranacademy.quran.data.database.models.AyahBoundsModel
import org.quranacademy.quran.data.database.models.AyahMarkerPositionModel
import org.quranacademy.quran.data.database.models.PageBoundsModel
import org.quranacademy.quran.data.database.models.SurahHeaderBoundsModel
import javax.inject.Inject

class PageBoundsDao @Inject constructor(
        private val mushafPageBoundsDatabaseAdapter: MushafPageBoundsDatabaseAdapter
) {

    fun getPageBounds(pageNumber: Int): PageBoundsModel {
        return mushafPageBoundsDatabaseAdapter.getPageBounds(pageNumber)
    }

    fun getPageAyahsBounds(pageNumber: Int): List<AyahBoundsModel> {
        return mushafPageBoundsDatabaseAdapter.gePageAyahsBounds(pageNumber)
    }

    fun getAyahMarkerBounds(pageNumber: Int): List<AyahMarkerPositionModel> {
        return mushafPageBoundsDatabaseAdapter.getAyahMarkerBounds(pageNumber)
    }

    fun getSurahHeaderBounds(pageNumber: Int): List<SurahHeaderBoundsModel> {
        return mushafPageBoundsDatabaseAdapter.getSurahHeaderBounds(pageNumber)
    }

}