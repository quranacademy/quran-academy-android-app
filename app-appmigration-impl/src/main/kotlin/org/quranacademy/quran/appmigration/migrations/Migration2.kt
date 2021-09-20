package org.quranacademy.quran.appmigration.migrations

import org.quranacademy.quran.appmigration.AppMigration
import org.quranacademy.quran.appmigration.PreferencesMigrator
import org.quranacademy.quran.data.PathProvider
import java.io.File
import javax.inject.Inject

class Migration2 @Inject constructor(
        private val preferencesMigrator: PreferencesMigrator,
        private val pathProvider: PathProvider
) : AppMigration(2) {

    override fun apply() {
        deleteOldDatabaseFileWithQuranArabicText()
        renameInitialSetupField()
        deleteDataLastUpdateTimeField()
    }

    private fun deleteOldDatabaseFileWithQuranArabicText() {
        File(pathProvider.databasesFolder, "quran-data.db").delete()
    }

    private fun renameInitialSetupField() {
        preferencesMigrator.renameBoolean("initial_setup_completed", "translations_initial_setup_completed")
    }

    private fun deleteDataLastUpdateTimeField() {
        if (preferencesMigrator.getLong("data_last_update", -1L) != -1L) {
            preferencesMigrator.setBoolean("initial_setup_completed", true)
        }
        preferencesMigrator.removeField("data_last_update")
    }

}