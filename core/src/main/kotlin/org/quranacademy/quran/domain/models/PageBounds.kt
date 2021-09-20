package org.quranacademy.quran.domain.models

import org.quranacademy.quran.domain.models.bounds.AyahBounds
import org.quranacademy.quran.domain.models.bounds.AyahMarkerPosition
import org.quranacademy.quran.domain.models.bounds.SurahHeaderBounds

data class PageBounds private constructor(
        val minX: Int,
        val minY: Int,
        val maxX: Int,
        val maxY: Int,
        val pageAyahsBounds: Map<AyahId, MutableList<AyahBounds>>,
        val ayahMarkerPositions: List<AyahMarkerPosition>,
        val surahHeaderBounds: List<SurahHeaderBounds>
) {

    class Builder(
            private val minX: Int,
            private val minY: Int,
            private val maxX: Int,
            private val maxY: Int
    ) {
        private val pageAyahsBounds = mutableMapOf<AyahId, MutableList<AyahBounds>>()
        private val ayahMarkerPositions = mutableListOf<AyahMarkerPosition>()
        private val surahHeaderBounds = mutableListOf<SurahHeaderBounds>()

        fun addAyahBounds(surahNumber: Int, ayahNumber: Int, bounds: AyahBounds) {
            val ayahId = AyahId(surahNumber, ayahNumber)
            val ayahBounds = pageAyahsBounds.getOrPut(ayahId) { mutableListOf() }
            val lastAyahBounds = ayahBounds.lastOrNull()
            if (lastAyahBounds != null && lastAyahBounds.lineNumber == bounds.lineNumber) {
                lastAyahBounds.engulf(bounds)
            } else {
                ayahBounds.add(bounds)
            }
        }

        fun addAyahMarkerPosition(positions: AyahMarkerPosition) {
            ayahMarkerPositions.add(positions)
        }

        fun addSurahHeaderBounds(bounds: SurahHeaderBounds) {
            surahHeaderBounds.add(bounds)
        }

        fun build(): PageBounds {
            return PageBounds(
                    minX = minX,
                    minY = minY,
                    maxX = maxX,
                    maxY = maxY,
                    pageAyahsBounds = pageAyahsBounds,
                    ayahMarkerPositions = ayahMarkerPositions,
                    surahHeaderBounds = surahHeaderBounds
            )
        }

    }

}
