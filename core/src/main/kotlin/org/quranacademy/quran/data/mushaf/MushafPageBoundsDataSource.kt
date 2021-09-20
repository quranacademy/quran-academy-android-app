package org.quranacademy.quran.data.mushaf

import org.quranacademy.quran.domain.models.PageBounds

interface MushafPageBoundsDataSource {

    fun getPageBounds(pageNumber: Int): PageBounds

}