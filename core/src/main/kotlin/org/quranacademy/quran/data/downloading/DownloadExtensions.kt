package org.quranacademy.quran.data.downloading

import com.downloader.PRDownloader
import com.downloader.request.DownloadRequest
import com.downloader.request.DownloadRequestBuilder
import org.quranacademy.quran.domain.models.FileDownloadInfo
import java.io.File

fun downloadFile(fileUrl: String, destination: File): DownloadRequestBuilder {
    return PRDownloader.download(fileUrl, destination.parent, destination.name)
}

fun DownloadRequest.setHQAOnProgressListener(
        listener: ((progress: FileDownloadInfo) -> Unit)?
): DownloadRequest {
    return setOnProgressListener {
        listener?.invoke(FileDownloadInfo(it.currentBytes, it.totalBytes))
    }
}

fun DownloadRequest.execute() {
    val response = executeSync()
    if (!response.isSuccessful && !response.isCancelled) {
        throw response.error.wrapError()
    }

    if (response.isCancelled) {
        throw DownloadedCancelledException()
    }
}

fun com.downloader.Error.wrapError() = DownloadException(this)
