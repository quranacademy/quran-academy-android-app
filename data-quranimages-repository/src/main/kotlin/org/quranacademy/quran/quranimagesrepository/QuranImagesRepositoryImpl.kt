package org.quranacademy.quran.quranimagesrepository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.withContext
import org.quranacademy.quran.data.mushaf.ImageFilePathProvider
import org.quranacademy.quran.data.mushaf.QuranImageFilesChecker
import org.quranacademy.quran.data.prefs.AppPreferences
import org.quranacademy.quran.domain.models.FileDownloadInfo
import org.quranacademy.quran.domain.models.MushafPageType
import org.quranacademy.quran.domain.repositories.QuranImagesRepository
import java.io.File
import javax.inject.Inject

class QuranImagesRepositoryImpl @Inject constructor(
        private val appPreferences: AppPreferences,
        private val imageFilePathProvider: ImageFilePathProvider,
        private val quranImageFilesChecker: QuranImageFilesChecker,
        private val imagesBundleDownloader: ImagesBundleDownloader,
        private val quranImageDownloader: QuranImageDownloader
) : QuranImagesRepository {

    private val mushafTypeChangingUpdates = BroadcastChannel<Unit>(1)

    override suspend fun isImagesDownloadingNeeded(): Boolean = withContext(Dispatchers.IO) {
        val suggestDownloadImagesBundle = appPreferences.suggestDownloadImagesBundle()
        isAllPageImagesDownloaded() && suggestDownloadImagesBundle
    }

    override fun getPageImageFile(pageNumber: Int, pageType: MushafPageType?): File {
        return imageFilePathProvider.getPageImageFile(pageNumber, pageType)
    }

    override suspend fun downloadPageImage(
            pageNumber: Int,
            pageType: MushafPageType?,
            onProgress: ((FileDownloadInfo) -> Unit)?
    ) = withContext(Dispatchers.IO) {
        val resultPageType = pageType ?: appPreferences.getMushafType()
        quranImageDownloader.download(pageNumber, resultPageType, onProgress)
    }

    override suspend fun downloadImagesBundle(
            onProgress: (FileDownloadInfo) -> Unit
    ) = withContext(Dispatchers.IO) {
        imagesBundleDownloader.download(onProgress)
    }

    override suspend fun cancelImagesBundleDownloading() = withContext(Dispatchers.IO) {
        imagesBundleDownloader.cancelDownloading()
    }

    override suspend fun isAllPageImagesDownloaded(): Boolean {
        return quranImageFilesChecker.haveAllImages()
    }

    override suspend fun disableImagesBundleDownloadingSuggestion() = withContext(Dispatchers.IO) {
        appPreferences.setSuggestDownloadImagesBundle(false)
    }

    override suspend fun setMushafPageType(pageType: MushafPageType) {
        appPreferences.setMushafPageType(pageType)
        mushafTypeChangingUpdates.send(Unit)
    }

    override fun getMushafType(): MushafPageType {
        return appPreferences.getMushafType()
    }

    override fun getMushafTypeChangingUpdates(): Flow<Unit> {
        return mushafTypeChangingUpdates.asFlow()
    }

}