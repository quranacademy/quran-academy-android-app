package org.quranacademy.quran.data.downloading

import com.downloader.Error

class DownloadException(
        val error: Error
) : RuntimeException(
        "Response code: ${error.responseCode}" +
                "\nMessage: ${error.serverErrorMessage}",
        error.connectionException
)