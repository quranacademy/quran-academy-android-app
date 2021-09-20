package org.quranacademy.quran.radio.data

object RadioConstants {

    // mime types and charsets and file extensions
    const val CHARSET_UNDEFINDED = "undefined"
    const val MIME_TYPE_JPG = "image/jpeg"
    const val MIME_TYPE_PNG = "image/png"
    const val MIME_TYPE_MPEG = "audio/mpeg"
    const val MIME_TYPE_HLS = "application/vnd.apple.mpegurl.audio"
    const val MIME_TYPE_M3U = "audio/x-mpegurl"
    const val MIME_TYPE_PLS = "audio/x-scpls"
    const val MIME_TYPE_XML = "text/xml"
    const val MIME_TYPE_OCTET_STREAM = "application/octet-stream"
    const val MIME_TYPE_UNSUPPORTED = "unsupported"

    val MIME_TYPES_M3U = arrayOf("application/mpegurl", "application/x-mpegurl", "audio/mpegurl", "audio/x-mpegurl")
    val MIME_TYPES_PLS = arrayOf("audio/x-scpls", "application/pls+xml")
    val MIME_TYPES_HLS = arrayOf("application/vnd.apple.mpegurl", "application/vnd.apple.mpegurl.audio")

}