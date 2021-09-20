package org.quranacademy.quran.appmigration.migrations

import android.util.Log
import org.quranacademy.quran.appmigration.AppMigration
import org.quranacademy.quran.appmigration.PreferencesMigrator
import org.quranacademy.quran.data.database.adapters.TranslationDatabaseManager
import org.quranacademy.quran.data.database.models.AyahTranslationModel_Table
import javax.inject.Inject

class Migration4 @Inject constructor(
        private val preferencesMigrator: PreferencesMigrator,
        private val translationDatabaseManager: TranslationDatabaseManager
) : AppMigration(4) {

    override fun apply() {
        preferencesMigrator.removeField("translations_initial_setup_completed")
        addFtsToTranslations()
    }

    private fun addFtsToTranslations() {
        translationDatabaseManager.getAdapters().forEach { adapter ->
            try {
                adapter.execSql("ALTER TABLE translation RENAME TO old_translations;")
                adapter.execSql("CREATE VIRTUAL TABLE IF NOT EXISTS translation USING FTS3(" +
                        "${AyahTranslationModel_Table.sura.nameAlias} INTEGER, " +
                        "${AyahTranslationModel_Table.ayat.nameAlias} INTEGER, " +
                        "${AyahTranslationModel_Table.text.nameAlias} TEXT, " +
                        "${AyahTranslationModel_Table.is_new_format.nameAlias} INTEGER" +
                        ")")
                adapter.execSql("INSERT INTO translation SELECT * from old_translations")
                adapter.execSql("DROP TABLE old_translations")
            } catch (error: Exception) {
                Log.d("HQA", "error (${adapter.getTranslation().name})", error)
            }
        }
    }

}