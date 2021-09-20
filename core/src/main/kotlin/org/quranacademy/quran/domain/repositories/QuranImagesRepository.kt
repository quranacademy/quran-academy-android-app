package org.quranacademy.quran.domain.repositories

import kotlinx.coroutines.flow.Flow
import org.quranacademy.quran.domain.models.FileDownloadInfo
import org.quranacademy.quran.domain.models.MushafPageType
import java.io.File

interface QuranImagesRepository {

    suspend fun isImagesDownloadingNeeded(): Boolean

    fun getPageImageFile(pageNumber: Int, pageType: MushafPageType? = null): File

    suspend fun downloadPageImage(
            pageNumber: Int,
            pageType: MushafPageType? = null,
            onProgress: ((FileDownloadInfo) -> Unit)? = null
    )

    suspend fun disableImagesBundleDownloadingSuggestion()

    suspend fun cancelImagesBundleDownloading()

    suspend fun downloadImagesBundle(onProgress: (FileDownloadInfo) -> Unit)

    suspend fun isAllPageImagesDownloaded(): Boolean

    suspend fun setMushafPageType(pageType: MushafPageType)

    fun getMushafType(): MushafPageType

    fun getMushafTypeChangingUpdates(): Flow<Unit>

}