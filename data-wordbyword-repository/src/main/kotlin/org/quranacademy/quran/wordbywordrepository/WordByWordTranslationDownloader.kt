package org.quranacademy.quran.wordbywordrepository

import com.downloader.request.DownloadRequest
import net.lingala.zip4j.core.ZipFile
import net.lingala.zip4j.exception.ZipException
import org.quranacademy.quran.data.PathProvider
import org.quranacademy.quran.data.downloading.downloadFile
import org.quranacademy.quran.data.downloading.execute
import org.quranacademy.quran.data.downloading.setHQAOnProgressListener
import org.quranacademy.quran.domain.exceptions.NoSpaceLeftException
import org.quranacademy.quran.domain.models.FileDownloadInfo
import org.quranacademy.quran.domain.models.WordByWordTranslation
import java.io.File
import javax.inject.Inject

class WordByWordTranslationDownloader @Inject constructor(
        private val pathProvider: PathProvider
) {

    private val downloadTasks: HashMap<String, DownloadRequest> = hashMapOf()

    suspend fun download(
            translation: WordByWordTranslation,
            isUpdating: Boolean,
            onProgress: (FileDownloadInfo) -> Unit
    ) {
        val fileUrl = translation.fileUrl
        val isZip = fileUrl.endsWith("zip")
        val destinationFileName = translation.fileName + if (isZip) ".zip" else ""
        val destinationFile = File(pathProvider.databasesFolder, destinationFileName)

        val task = downloadFile(fileUrl, destinationFile)
                .build()
                .setOnCancelListener { destinationFile.delete() }
                .setHQAOnProgressListener(onProgress)

        downloadTasks[translation.fileUrl] = task

        task.execute()
        onTranslationFileDownloaded(destinationFile, isZip, isUpdating, translation)
    }

    fun cancelDownloading(translation: WordByWordTranslation) {
        val downloadTask = downloadTasks.remove(translation.fileUrl)
        downloadTask?.cancel()
    }

    private fun onTranslationFileDownloaded(
            downloadedFile: File,
            isZip: Boolean,
            isUpdating: Boolean,
            translation: WordByWordTranslation
    ) {
        val newDbFile = if (isZip) {
            //extract DB file fle from downloaded archive and delete zip-file
            val extractedDbFileName = translation.name + if (isUpdating) "-update" else ""
            extractDatabaseFile(downloadedFile, extractedDbFileName)
            downloadedFile.delete()
            File(downloadedFile.parent, extractedDbFileName)
        } else {
            downloadedFile
        }

        if (isUpdating) {
            val oldTranslationFile = File(downloadedFile.parent, translation.fileName)
            oldTranslationFile.delete()
            newDbFile.renameTo(oldTranslationFile)
        }
    }

    private fun extractDatabaseFile(zipFile: File, databaseFileName: String): String {
        val parentFolderPath = zipFile.parent
        try {
            ZipFile(zipFile).extractFile(databaseFileName, parentFolderPath)
            zipFile.delete()
        } catch (e: ZipException) {
            throw NoSpaceLeftException()
        }

        return File(parentFolderPath, databaseFileName).absolutePath
    }

}