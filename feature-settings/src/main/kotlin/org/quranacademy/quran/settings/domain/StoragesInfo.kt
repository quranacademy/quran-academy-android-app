package org.quranacademy.quran.settings.domain

data class StoragesInfo(
        val internalStorage: Storage,
        val externalStorage: Storage?,
        val currentStoragePath: String,
        val usedAppSpace: Long
) {

    fun isCurrentStorageExternal(): Boolean {
        return currentStoragePath == internalStorage.folderPath
    }

    fun isExternalStorageMounted() = externalStorage != null

    fun isCurrentStorageMounted(): Boolean {
        return if (!isCurrentStorageExternal()) {
            true
        } else currentStoragePath == externalStorage?.folderPath
    }

    data class Storage(
            val folderPath: String,
            val freeSpace: Long
    )

}

