package org.quranacademy.quran.translationsrepository

import com.downloader.request.DownloadRequest
import net.lingala.zip4j.core.ZipFile
import net.lingala.zip4j.exception.ZipException
import org.quranacademy.quran.data.PathProvider
import org.quranacademy.quran.data.downloading.downloadFile
import org.quranacademy.quran.data.downloading.execute
import org.quranacademy.quran.data.downloading.setHQAOnProgressListener
import org.quranacademy.quran.domain.exceptions.NoSpaceLeftException
import org.quranacademy.quran.domain.models.FileDownloadInfo
import org.quranacademy.quran.domain.models.Translation
import java.io.File
import javax.inject.Inject

class TranslationDownloader @Inject constructor(
        private val pathProvider: PathProvider
) {

    private val downloadTasks: HashMap<String, TaskWrapper> = hashMapOf()

    fun getDownloadingTranslationCodes(): List<String> = downloadTasks.map { it.key }

    fun setCurrentTranslationsDownloadingListener(
            listener: (code: String, progress: FileDownloadInfo) -> Unit
    ) {
        downloadTasks.entries.map {
            it.value.addListener { progress ->
                listener(it.key, progress)
            }
        }
    }

    fun download(
            translation: Translation,
            isUpdating: Boolean,
            onDownloadProgress: (FileDownloadInfo) -> Unit
    ) {
        val fileUrl = translation.fileUrl
        val filePostFix = if (isUpdating) "-update" else ""
        val destinationFileName = translation.fileName + filePostFix
        val destinationFile = File(pathProvider.databasesFolder, destinationFileName)

        val downloadTask = downloadFile(fileUrl, destinationFile)
                .build()
                .setOnCancelListener {
                    downloadTasks.remove(translation.code)
                    destinationFile.delete()
                }
                .let { TaskWrapper(it) }

        downloadTasks[translation.code] = downloadTask
        downloadTask.addListener(onDownloadProgress)

        try {
            downloadTask.start()
            val isZip = fileUrl.endsWith("zip")
            onTranslationFileDownloaded(destinationFile, isZip, isUpdating, translation)
            downloadTask.onFinished()
        } catch (error: Exception) {
            downloadTasks.remove(translation.code)
            throw error
        }
    }

    fun cancelDownloading(translation: Translation) {
        val downloadTask = downloadTasks.remove(translation.code)
        downloadTask?.cancel()
    }

    private fun onTranslationFileDownloaded(
            downloadedFile: File,
            isZip: Boolean,
            isUpdating: Boolean,
            translation: Translation
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
        downloadTasks.remove(translation.code)
    }

    private fun extractDatabaseFile(zipFile: File, extractedDbFileName: String) {
        try {
            ZipFile(zipFile).extractFile(extractedDbFileName, zipFile.parent)
        } catch (e: ZipException) {
            throw NoSpaceLeftException()
        }
    }

    private class TaskWrapper(val task: DownloadRequest) {

        private val listeners = mutableListOf<(FileDownloadInfo) -> Unit>()

        init {
            task.setHQAOnProgressListener(this::onProgress)
        }

        fun start() {
            task.execute()
        }

        fun addListener(listener: (FileDownloadInfo) -> Unit) {
            listeners.add(listener)
        }

        fun onProgress(downloadInfo: FileDownloadInfo) {
            listeners.forEach {
                it(downloadInfo)
            }
        }

        fun onFinished() {
            listeners.forEach {
                it(FileDownloadInfo(
                        totalSize = FileDownloadInfo.IS_FINISHED,
                        downloadedSize = FileDownloadInfo.IS_FINISHED
                ))
            }
        }

        fun cancel() {
            task.cancel()
        }

    }

}