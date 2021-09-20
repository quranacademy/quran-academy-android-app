package org.quranacademy.quran.quranimagesrepository

import com.downloader.PRDownloader
import com.downloader.request.DownloadRequest
import net.lingala.zip4j.core.ZipFile
import org.quranacademy.quran.data.PathProvider
import org.quranacademy.quran.data.downloading.QuranEndpointsProvider
import org.quranacademy.quran.data.downloading.downloadFile
import org.quranacademy.quran.data.downloading.execute
import org.quranacademy.quran.data.downloading.setHQAOnProgressListener
import org.quranacademy.quran.domain.models.FileDownloadInfo
import java.io.File
import javax.inject.Inject

class ImagesBundleDownloader @Inject constructor(
        private val pathProvider: PathProvider,
        private val quranEndpointsProvider: QuranEndpointsProvider
) {

    private var imagesDownloadRequest: DownloadRequest? = null

    fun download(onProgress: (FileDownloadInfo) -> Unit) {
        val fileUrl = quranEndpointsProvider.getMushafBundleUrl()
        val destinationFile = File(pathProvider.imagesFolder, "images.zip")

        this.imagesDownloadRequest = downloadFile(fileUrl, destinationFile)
                .build()
                .setHQAOnProgressListener(onProgress)
                .also { it.execute() }
        extractImages(destinationFile)
    }

    fun cancelDownloading() {
        imagesDownloadRequest?.let {
            PRDownloader.cancel(imagesDownloadRequest?.downloadId)
        }
    }

    private fun extractImages(zipFile: File) {
        val imagesFolder = zipFile.parentFile
        val imagesBundleZip = ZipFile(zipFile)
        imagesBundleZip.extractAll(imagesFolder.absolutePath)
        zipFile.delete()
    }


}