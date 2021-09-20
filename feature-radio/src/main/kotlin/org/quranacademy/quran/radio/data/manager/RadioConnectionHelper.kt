package org.quranacademy.quran.radio.data.manager

import org.quranacademy.quran.radio.data.RadioConstants
import timber.log.Timber
import java.net.HttpURLConnection
import java.net.URL
import javax.inject.Inject

class RadioConnectionHelper @Inject constructor() {

    fun getRadioUrl(url: String): String {
        val connection = URL(url).openConnection() as HttpURLConnection
        connection.instanceFollowRedirects = false
        return connection.getHeaderField("Location")
    }

    /* Detects content type (mime type) from given URL string - async using coroutine - use only on separate threat */
    fun detectContentType(urlString: String): RadioContentType {
        val contentType = RadioContentType(RadioConstants.MIME_TYPE_UNSUPPORTED, RadioConstants.CHARSET_UNDEFINDED)
        val connection = createConnection(urlString)
        if (connection != null) {
            val contentTypeHeader: String = connection.contentType ?: String()
            val contentTypeHeaderParts: List<String> = contentTypeHeader.split(";")
            contentTypeHeaderParts.forEachIndexed { index, part ->
                if (index == 0 && part.isNotEmpty()) {
                    contentType.type = part.trim()
                } else if (part.contains("charset=")) {
                    contentType.charset = part.substringAfter("charset=").trim()
                }
            }

            // special treatment for octet-stream - try to get content type from file extension
            if (contentType.type.contains(RadioConstants.MIME_TYPE_OCTET_STREAM)) {
                Timber.w("Special case \"application/octet-stream\"")
                val headerFieldContentDisposition: String? = connection.getHeaderField("Content-Disposition")
                if (headerFieldContentDisposition != null) {
                    val fileName: String = headerFieldContentDisposition.split("=")[1].replace("\"", "") //getting value after '=' & stripping any "s
                    contentType.type = getContentTypeFromExtension(fileName)
                } else {
                    Timber.i("Unable to get file name from \"Content-Disposition\" header field.")
                }
            }

            connection.disconnect()
        }
        Timber.i("content type: ${contentType.type} | character set: ${contentType.charset}")
        return contentType
    }

    /* Creates a http connection from given url string */
    private fun createConnection(urlString: String, redirectCount: Int = 0): HttpURLConnection? {
        var connection: HttpURLConnection? = null

        try {
            // try to open connection and get status
            connection = URL(urlString).openConnection() as HttpURLConnection
            val status = connection.responseCode

            // CHECK for non-HTTP_OK status
            if (status != HttpURLConnection.HTTP_OK) {
                // CHECK for redirect status
                if (status == HttpURLConnection.HTTP_MOVED_TEMP || status == HttpURLConnection.HTTP_MOVED_PERM || status == HttpURLConnection.HTTP_SEE_OTHER) {
                    val redirectUrl: String = connection.getHeaderField("Location")
                    connection.disconnect()
                    if (redirectCount < 5) {
                        connection = createConnection(redirectUrl, redirectCount + 1)
                    } else {
                        connection = null
                    }
                }
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }

        return connection
    }

    /* Determine content type based on file extension */
    private fun getContentTypeFromExtension(fileName: String): String {
        if (fileName.endsWith("m3u", true)) return RadioConstants.MIME_TYPE_M3U
        if (fileName.endsWith("pls", true)) return RadioConstants.MIME_TYPE_PLS
        if (fileName.endsWith("png", true)) return RadioConstants.MIME_TYPE_PNG
        if (fileName.endsWith("jpg", true)) return RadioConstants.MIME_TYPE_JPG
        if (fileName.endsWith("jpeg", true)) return RadioConstants.MIME_TYPE_JPG
        // default return
        return RadioConstants.MIME_TYPE_UNSUPPORTED
    }

}