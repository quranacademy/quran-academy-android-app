package org.quranacademy.quran.data

import android.content.Context
import org.quranacademy.quran.data.prefs.AppPreferences
import org.quranacademy.quran.domain.models.MushafPageType
import java.io.File
import javax.inject.Inject

class PathProviderImpl @Inject constructor(
        private val context: Context,
        private val appPreferences: AppPreferences
) : PathProvider {

    override val databasesFolder: File
        get() {
            return File(getAppFolder(), "databases/")
        }
    override val imagesFolder: File
        get() {
            val mushafPageType = appPreferences.getMushafType()
            return getImagesFolderForType(mushafPageType)
        }

    override val quranArabicTextsDatabase: File
        get() {
            return File(databasesFolder, "quran-arabic-text.db")
        }
    override val mushafPageBoundsDatabase: File
        get() {
            val mushafPageType = appPreferences.getMushafType()
            return getPageBoundsDatabaseFile(mushafPageType)
        }

    override fun getPageBoundsDatabaseFile(type: MushafPageType): File {
        return File(databasesFolder, "ayah_bounds/${type.code}.db")
    }

    override fun getImagesFolderForType(type: MushafPageType): File {
        return File(getAppFolder(), "images/${type.code}")
    }

    override fun getAppFolder(): File {
        return appPreferences.getAppDataFilePath()?.let {
            File(it)
        } ?: context.filesDir
    }


}