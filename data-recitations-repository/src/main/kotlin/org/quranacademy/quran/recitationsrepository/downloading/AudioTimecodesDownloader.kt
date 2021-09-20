package org.quranacademy.quran.recitationsrepository.downloading

import com.downloader.PRDownloader
import okhttp3.internal.http2.StreamResetException
import org.quranacademy.quran.data.downloading.downloadFile
import org.quranacademy.quran.data.downloading.execute
import org.quranacademy.quran.data.downloading.setHQAOnProgressListener
import org.quranacademy.quran.domain.models.FileDownloadInfo
import org.quranacademy.quran.domain.models.Recitation
import org.quranacademy.quran.recitationsrepository.TimecodesPathProvider
import java.net.ConnectException
import java.net.UnknownHostException
import javax.inject.Inject

class AudioTimecodesDownloader @Inject constructor(
        private val pathProvider: TimecodesPathProvider
) {

    private var currentTaskId: Int? = null

    suspend fun downloadAudioTimeCodes(
            recitation: Recitation,
            onProgressListener: ((FileDownloadInfo) -> Unit)? = null
    ) {
        val timeCodesFileUrl = recitation.timecodesFileUrl!!
        val destinationDatabaseFile = pathProvider.getTimecodesDbFile(recitation.id)

        try {
            downloadFile(timeCodesFileUrl, destinationDatabaseFile)
                    .build()
                    .setHQAOnProgressListener(onProgressListener)
                    .execute()
        } catch (error: Exception) {
            throw wrapError(error, timeCodesFileUrl)
        }
    }

    fun cancelDownloading() {
        PRDownloader.cancel(currentTaskId)
    }

    private fun wrapError(error: Throwable, audioFileUrl: String): AudioDownloadException {
        return when (error) {
            //is ServerCanceledException -> AudioDownloadException.IncorrectAudioUrl("Audio file URL: $audioFileUrl", cause = error)
            is UnknownHostException, is ConnectException, is StreamResetException -> AudioDownloadException.NoNetwork(cause = error)
            else -> AudioDownloadException.Unknown(cause = error)
        }
    }

}