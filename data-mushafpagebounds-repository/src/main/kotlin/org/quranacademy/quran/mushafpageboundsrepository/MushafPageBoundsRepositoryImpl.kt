package org.quranacademy.quran.mushafpageboundsrepository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.quranacademy.quran.data.PathProvider
import org.quranacademy.quran.data.database.adapters.MushafPageBoundsDatabaseAdapter
import org.quranacademy.quran.data.mushaf.MushafPageBoundsDataSource
import org.quranacademy.quran.data.prefs.AppPreferences
import org.quranacademy.quran.domain.models.FileDownloadInfo
import org.quranacademy.quran.domain.models.MushafPageType
import org.quranacademy.quran.domain.models.PageBounds
import org.quranacademy.quran.domain.repositories.MushafPageBoundsRepository
import javax.inject.Inject

class MushafPageBoundsRepositoryImpl @Inject constructor(
        private val appPreferences: AppPreferences,
        private val pathProvider: PathProvider,
        private val pageBoundsDataDownloader: PageBoundsDataDownloader,
        private val mushafPageBoundsDataSource: MushafPageBoundsDataSource,
        private val mushafPageBoundsDatabaseAdapter: MushafPageBoundsDatabaseAdapter
) : MushafPageBoundsRepository {

    override fun isBoundsDatabaseDownloaded(type: MushafPageType): Boolean {
        return pathProvider.getPageBoundsDatabaseFile(type).exists()
    }

    override suspend fun downloadAyahBoundsData(
            type: MushafPageType?,
            onProgress: (FileDownloadInfo) -> Unit
    ) = withContext(Dispatchers.IO) {
        val resultType = type ?: appPreferences.getMushafType()
        pageBoundsDataDownloader.download(resultType, onProgress)
    }

    override fun reconnectToBoundsDatabase() {
        mushafPageBoundsDatabaseAdapter.disconnect()
        mushafPageBoundsDatabaseAdapter.connect()
    }

    override suspend fun getPageBounds(pageNumber: Int): PageBounds {
        return mushafPageBoundsDataSource.getPageBounds(pageNumber)
    }

}