package org.quranacademy.quran.data

import org.quranacademy.quran.domain.models.MushafPageType
import java.io.File

interface PathProvider {

    val databasesFolder: File
    val imagesFolder: File
    val quranArabicTextsDatabase: File
    val mushafPageBoundsDatabase: File

    fun getPageBoundsDatabaseFile(type: MushafPageType): File

    fun getImagesFolderForType(type: MushafPageType): File

    fun getAppFolder(): File

}