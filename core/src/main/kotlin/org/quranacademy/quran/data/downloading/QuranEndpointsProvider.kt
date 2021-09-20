package org.quranacademy.quran.data.downloading

import org.quranacademy.quran.domain.models.MushafPageType

interface QuranEndpointsProvider {

    fun getMushafBundleUrl(): String

    fun getMushafImageUrl(
            pageNumber: Int,
            mushafPageType: MushafPageType
    ): String

    fun getMushafAyahBoundsUrl(mushafPageType: MushafPageType): String

}