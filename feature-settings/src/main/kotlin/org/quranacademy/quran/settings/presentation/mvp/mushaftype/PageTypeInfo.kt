package org.quranacademy.quran.settings.presentation.mvp.mushaftype

import org.quranacademy.quran.domain.models.MushafPageType
import java.io.File

class PageTypeInfo(
        val type: MushafPageType,
        val previewImageFile: File,
        val title: String,
        val description: String,
        val isBoundsDatabaseDownloaded: Boolean
) {

    private var imageDownloadListener: (() -> Unit)? = null

    @Volatile
    private var isImageDownloaded = previewImageFile.exists()

    fun isImageDownloaded() = isImageDownloaded

    fun onImageDownloadListener(listener: () -> Unit) {
        this.imageDownloadListener = listener
        if (isImageDownloaded) {
            listener()
        }
    }

    fun onImageDownloaded() {
        this.isImageDownloaded = true
        imageDownloadListener?.invoke()
    }

}