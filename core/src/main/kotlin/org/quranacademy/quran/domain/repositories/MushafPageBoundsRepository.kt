package org.quranacademy.quran.domain.repositories

import org.quranacademy.quran.domain.models.FileDownloadInfo
import org.quranacademy.quran.domain.models.MushafPageType
import org.quranacademy.quran.domain.models.PageBounds

interface MushafPageBoundsRepository {

    fun isBoundsDatabaseDownloaded(type: MushafPageType): Boolean

    suspend fun downloadAyahBoundsData(
            type: MushafPageType? = null,
            onProgress: (FileDownloadInfo) -> Unit
    )

    suspend fun getPageBounds(pageNumber: Int): PageBounds

    fun reconnectToBoundsDatabase()

}