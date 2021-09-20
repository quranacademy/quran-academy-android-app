package org.quranacademy.quran.mushafpageboundsrepository

import net.lingala.zip4j.core.ZipFile
import net.lingala.zip4j.model.FileHeader
import org.quranacademy.quran.data.PathProvider
import org.quranacademy.quran.data.downloading.QuranEndpointsProvider
import org.quranacademy.quran.data.downloading.downloadFile
import org.quranacademy.quran.data.downloading.execute
import org.quranacademy.quran.data.downloading.setHQAOnProgressListener
import org.quranacademy.quran.domain.models.FileDownloadInfo
import org.quranacademy.quran.domain.models.MushafPageType
import java.io.File
import javax.inject.Inject


class PageBoundsDataDownloader @Inject constructor(
        private val pathProvider: PathProvider,
        private val quranUrlProvider: QuranEndpointsProvider
) {

    fun download(
            mushafPageType: MushafPageType,
            onProgress: (FileDownloadInfo) -> Unit
    ) {
        val fileUrl = quranUrlProvider.getMushafAyahBoundsUrl(mushafPageType)
        val destinationDatabaseFile = pathProvider.getPageBoundsDatabaseFile(mushafPageType)
        val destinationZipFile = File("${destinationDatabaseFile.absolutePath}.zip")

        val downloadId = downloadFile(fileUrl, destinationZipFile)
                .build()
                .setHQAOnProgressListener(onProgress)
                .execute()
        extractDatabase(destinationDatabaseFile, destinationZipFile)
    }

    private fun extractDatabase(
            databaseFile: File,
            pageBoundsZipFile: File
    ) {
        val pageBoundsZip = ZipFile(pageBoundsZipFile)

        val destinationFolder = pathProvider.databasesFolder.absolutePath
        val archivedDatabaseFile = (pageBoundsZip.fileHeaders[0] as FileHeader)
        pageBoundsZip.extractFile(archivedDatabaseFile, destinationFolder)

        val extractedFile = File(destinationFolder, archivedDatabaseFile.fileName)
        extractedFile.renameTo(databaseFile)
        pageBoundsZipFile.delete()
    }

}