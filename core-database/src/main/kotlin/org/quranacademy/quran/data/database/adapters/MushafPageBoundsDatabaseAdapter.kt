package org.quranacademy.quran.data.database.adapters

import org.quranacademy.quran.data.database.models.AyahBoundsModel
import org.quranacademy.quran.data.database.models.AyahMarkerPositionModel
import org.quranacademy.quran.data.database.models.PageBoundsModel
import org.quranacademy.quran.data.database.models.SurahHeaderBoundsModel
import org.quranacademy.quran.domain.models.AyahId

interface MushafPageBoundsDatabaseAdapter {

    fun connect()

    fun getPageBounds(pageNumber: Int): PageBoundsModel

    fun gePageAyahsBounds(pageNumber: Int): List<AyahBoundsModel>

    fun getAyahMarkerBounds(pageNumber: Int): List<AyahMarkerPositionModel>

    fun getSurahHeaderBounds(pageNumber: Int): List<SurahHeaderBoundsModel>

    fun getPageAyahs(pageNumber: Int): List<AyahId>

    fun disconnect()

}