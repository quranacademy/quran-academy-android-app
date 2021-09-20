package org.quranacademy.quran.data

import android.content.Context
import java.io.File
import javax.inject.Inject

/**
 * Извлекает файлы из папки assets в файловую систему
 */
class AssetsFileExtractor @Inject constructor(
        private val context: Context
) {

    fun extract(assetsFileName: String, destinationFile: File, deleteIfExists: Boolean = true) {
        //destination file is exists and we don't have to delete it
        if (destinationFile.exists() && !deleteIfExists) {
            return
        }
        val tempFile = File(destinationFile.parent, destinationFile.name + ".temp")
        tempFile.delete() //delete old file
        tempFile.parentFile.mkdirs()

        val inputStream = context.assets.open(assetsFileName)
        inputStream.use { input ->
            tempFile.outputStream().use { fileOut ->
                input.copyTo(fileOut)
            }
        }

        destinationFile.delete() //delete old file
        tempFile.renameTo(destinationFile)
    }

}
