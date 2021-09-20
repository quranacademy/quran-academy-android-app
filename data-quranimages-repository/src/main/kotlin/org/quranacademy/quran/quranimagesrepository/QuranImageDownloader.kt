package org.quranacademy.quran.quranimagesrepository

import org.quranacademy.quran.data.downloading.QuranEndpointsProvider
import org.quranacademy.quran.data.downloading.downloadFile
import org.quranacademy.quran.data.downloading.execute
import org.quranacademy.quran.data.downloading.setHQAOnProgressListener
import org.quranacademy.quran.data.mushaf.ImageFilePathProvider
import org.quranacademy.quran.domain.models.FileDownloadInfo
import org.quranacademy.quran.domain.models.MushafPageType
import javax.inject.Inject

class QuranImageDownloader @Inject constructor(
        private val imageFilePathProvider: ImageFilePathProvider,
        private val quranEndpointsProvider: QuranEndpointsProvider
) {

    fun download(
            pageNumber: Int,
            pageType: MushafPageType,
            onProgress: ((FileDownloadInfo) -> Unit)?
    ) {
        val fileUrl = quranEndpointsProvider.getMushafImageUrl(pageNumber, pageType)
        val destinationFile = imageFilePathProvider.getPageImageFile(pageNumber, pageType)

        downloadFile(fileUrl, destinationFile)
                .build()
                .setHQAOnProgressListener(onProgress)
                .execute()
    }

}