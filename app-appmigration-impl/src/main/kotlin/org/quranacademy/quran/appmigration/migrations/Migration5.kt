package org.quranacademy.quran.appmigration.migrations

import org.quranacademy.quran.appmigration.AppMigration
import org.quranacademy.quran.appmigration.PreferencesMigrator
import org.quranacademy.quran.data.PathProvider
import org.quranacademy.quran.data.database.adapters.MushafPageBoundsDatabaseAdapter
import java.io.File
import javax.inject.Inject

class Migration5 @Inject constructor(
        private val preferencesMigrator: PreferencesMigrator,
        private val pageBoundsAdapter: MushafPageBoundsDatabaseAdapter,
        private val pathProvider: PathProvider
) : AppMigration(5) {

    override fun apply() {
        preferencesMigrator.removeField("app_install_id")

        moveMushafImagesToNewFolder()
    }

    private fun moveMushafImagesToNewFolder() {
        val oldImagesFolder = File(pathProvider.getAppFolder(), "images/")
        val newFolder = pathProvider.imagesFolder
        newFolder.mkdir()
        oldImagesFolder.listFiles()
                ?.filter { it.name != newFolder.name }
                ?.forEach { imageFile ->
                    val newImageFile = File(newFolder, imageFile.name)
                    imageFile.copyTo(newImageFile)
                    imageFile.delete()
                }

        pageBoundsAdapter.disconnect()
        val newBoundsFilePlace = pathProvider.mushafPageBoundsDatabase
        newBoundsFilePlace.parentFile!!.mkdirs()
        val oldBoundsFile = File(pathProvider.databasesFolder, "ayah_bounds.db")
        oldBoundsFile.copyTo(newBoundsFilePlace)
        oldBoundsFile.delete()
        val oldBoundsJournalFile = File(pathProvider.databasesFolder, "ayah_bounds.db-journal")
        oldBoundsJournalFile.delete()
        pageBoundsAdapter.connect()
    }

}