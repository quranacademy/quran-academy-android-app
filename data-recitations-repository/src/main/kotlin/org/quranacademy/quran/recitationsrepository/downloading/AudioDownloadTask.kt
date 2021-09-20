package org.quranacademy.quran.recitationsrepository.downloading

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.internal.http2.StreamResetException
import org.quranacademy.quran.data.downloading.downloadFile
import org.quranacademy.quran.data.downloading.execute
import org.quranacademy.quran.data.downloading.setHQAOnProgressListener
import org.quranacademy.quran.domain.models.FileDownloadInfo
import java.io.File
import java.net.ConnectException
import java.net.UnknownHostException

class AudioDownloadTask(
        private val fileUrl: String,
        private val filePath: String,
        private val onProgressListener: ((FileDownloadInfo) -> Unit)? = null
) {

    private var isCancelled = false

    suspend fun start() = withContext(Dispatchers.IO) {
        try {
            downloadFile(fileUrl, File(filePath))
                    .build()
                    .setHQAOnProgressListener(onProgressListener)
                    .execute()
        } catch (error: Exception) {
            throw wrapError(error)
        }
    }

    private fun wrapError(error: Throwable): AudioDownloadException {
        return when (error) {
            //is ServerCanceledException -> AudioDownloadException.IncorrectAudioUrl(
            //        "Audio file URL: $audioFileUrl", cause = error
            //)
            is UnknownHostException,
            is ConnectException,
            is StreamResetException -> AudioDownloadException.NoNetwork(cause = error)
            else -> AudioDownloadException.Unknown(cause = error)
        }
    }

    fun cancel() {
        isCancelled = true
    }

}