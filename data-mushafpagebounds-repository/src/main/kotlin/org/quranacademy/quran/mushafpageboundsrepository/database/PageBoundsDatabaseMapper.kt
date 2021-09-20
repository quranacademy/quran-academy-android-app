package org.quranacademy.quran.mushafpageboundsrepository.database

import org.quranacademy.quran.data.database.models.AyahBoundsModel
import org.quranacademy.quran.data.database.models.AyahMarkerPositionModel
import org.quranacademy.quran.data.database.models.PageBoundsModel
import org.quranacademy.quran.data.database.models.SurahHeaderBoundsModel
import org.quranacademy.quran.domain.models.PageBounds
import org.quranacademy.quran.domain.models.bounds.AyahBounds
import org.quranacademy.quran.domain.models.bounds.AyahMarkerPosition
import org.quranacademy.quran.domain.models.bounds.SurahHeaderBounds
import javax.inject.Inject

class PageBoundsDatabaseMapper @Inject constructor() {

    fun mapFromDatabase(
            pageBoundsModel: PageBoundsModel,
            pageAyahBounds: List<AyahBoundsModel>,
            ayahMarkerPositions: List<AyahMarkerPositionModel>,
            surahHeaderBounds: List<SurahHeaderBoundsModel>
    ): PageBounds {
        val pageBoundsBuilder = PageBounds.Builder(
                minX = pageBoundsModel.minX,
                minY = pageBoundsModel.minY,
                maxX = pageBoundsModel.maxX,
                maxY = pageBoundsModel.maxY
        )
        mapAyahBounds(pageBoundsBuilder, pageAyahBounds)
        mapAyahMarkerPositions(pageBoundsBuilder, ayahMarkerPositions)
        mapSurahHeaderBounds(pageBoundsBuilder, surahHeaderBounds)
        return pageBoundsBuilder.build()
    }

    private fun mapAyahBounds(
            pageBoundsBuilder: PageBounds.Builder,
            pageAyahBounds: List<AyahBoundsModel>
    ) {
        pageAyahBounds.forEach {
            pageBoundsBuilder.addAyahBounds(
                    it.suraNumber,
                    it.ayahNumber,
                    AyahBounds(
                            surahNumber = it.suraNumber,
                            ayahNumber = it.ayahNumber,
                            lineNumber = it.lineNumber,
                            position = it.position,
                            minX = it.minX,
                            minY = it.minY,
                            maxX = it.maxX,
                            maxY = it.maxY
                    )
            )
        }
    }

    private fun mapAyahMarkerPositions(
            pageBoundsBuilder: PageBounds.Builder,
            ayahMarkerPositions: List<AyahMarkerPositionModel>
    ) {
        ayahMarkerPositions.forEach {
            pageBoundsBuilder.addAyahMarkerPosition(
                    AyahMarkerPosition(
                            surahNumber = it.surahNumber,
                            ayahNumber = it.ayahNumber,
                            pageNumber = it.pageNumber,
                            x = it.x,
                            y = it.y
                    )
            )
        }
    }

    private fun mapSurahHeaderBounds(
            pageBoundsBuilder: PageBounds.Builder,
            surahHeaderBounds: List<SurahHeaderBoundsModel>
    ) {
        surahHeaderBounds.forEach {
            pageBoundsBuilder.addSurahHeaderBounds(
                    SurahHeaderBounds(
                            surahNumber = it.surahNumber,
                            pageNumber = it.pageNumber,
                            x = it.x,
                            y = it.y,
                            width = it.width,
                            height = it.height
                    )
            )
        }
    }

}