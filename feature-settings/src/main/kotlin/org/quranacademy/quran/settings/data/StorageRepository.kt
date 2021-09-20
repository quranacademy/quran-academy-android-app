package org.quranacademy.quran.settings.data

import android.content.Context
import androidx.core.content.ContextCompat
import com.tonyodev.storagegrapher.Storage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.quranacademy.quran.data.PathProvider
import org.quranacademy.quran.settings.domain.StoragesInfo
import javax.inject.Inject

class StorageRepository @Inject constructor(
        private val context: Context,
        private val pathProvider: PathProvider,
        private val appDataManager: AppDataManager
) {

    fun getStoragesInfo(): StoragesInfo {
        // #===== SD card ========#

        //We have three types of storages:
        // 1. Internal - private app folder
        // 2. External Emulated - part of device memory, that regarded as external
        // 3. External Removable - simple SD card

        //??? on pre-Marshmallow
        //val isMarshmallowOrAbove = Build.VERSION.SDK_INT >= Build.VERSION_CODES.M

        val primaryStorageInfo = Storage.getPrimaryStorageVolume()!!.let {
            StoragesInfo.Storage(
                    folderPath = context.filesDir.absolutePath,
                    freeSpace = it.freeSpace
            )
        }
        val externalStorageSdPath = ContextCompat.getExternalFilesDirs(context, null)
                .drop(1) // first storage - External Emulated
                .filterNotNull()
                .firstOrNull()

        val externalStorage = externalStorageSdPath?.let {
            val sdCardVolume = Storage.getStorageVolume(it)!!
            StoragesInfo.Storage(
                    folderPath = it.absolutePath,
                    freeSpace = sdCardVolume.freeSpace
            )
        }

        //Get app size. The App is located on the internal storage
        val currentStorage = pathProvider.getAppFolder()
        val usedAppSpace = Storage.getAppDirBytes(context).let {
            val isCurrentStoragePrimary = currentStorage.absolutePath == primaryStorageInfo.folderPath
            if (!isCurrentStoragePrimary) {
                //primary + external folders
                it + Storage.getDirectorySize(currentStorage)
            } else it
        }

        return StoragesInfo(
                internalStorage = primaryStorageInfo,
                externalStorage = externalStorage,
                currentStoragePath = currentStorage.absolutePath,
                usedAppSpace = usedAppSpace
        )
    }

    suspend fun setAppDataFilePath(destinationPath: String) = withContext(Dispatchers.IO) {
        val currentAppPath = getStoragesInfo().currentStoragePath
        appDataManager.moveDataTo(currentAppPath, destinationPath)
    }

}