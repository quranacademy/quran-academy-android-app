package org.quranacademy.quran.data.database.migrations

import com.raizlabs.android.dbflow.annotation.Migration
import com.raizlabs.android.dbflow.sql.migration.BaseMigration
import com.raizlabs.android.dbflow.structure.database.DatabaseWrapper
import org.quranacademy.quran.data.database.AppDatabase

@Migration(version = 5, database = AppDatabase::class)
class MigrationV5 : BaseMigration() {

    override fun migrate(database: DatabaseWrapper) {
        database.beginTransaction()
        val commands = listOf(
                "CREATE TEMPORARY TABLE `Translations_old`(" +
                        "`id` INTEGER," +
                        "`code` TEXT, " +
                        "`name` TEXT, " +
                        "`language_code` TEXT, " +
                        "`file_url` TEXT, " +
                        "`file_name` TEXT, " +
                        "`is_tafseer` INTEGER, " +
                        "`is_downloaded` INTEGER, " +
                        "`is_enabled` INTEGER, " +
                        "`remote_last_update_time` INTEGER, " +
                        "`local_last_update_time` INTEGER, " +
                        "PRIMARY KEY(`id`)" +
                        ")",
                "INSERT INTO Translations_old SELECT * FROM Translations;",
                "DROP TABLE Translations;",

                "CREATE TABLE IF NOT EXISTS `Translations`(" +
                        "`code` TEXT, " +
                        "`name` TEXT, " +
                        "`language_code` TEXT, " +
                        "`file_url` TEXT, " +
                        "`file_name` TEXT, " +
                        "`is_tafseer` INTEGER, " +
                        "`is_downloaded` INTEGER, " +
                        "`is_enabled` INTEGER, " +
                        "`remote_last_update_time` INTEGER, " +
                        "`local_last_update_time` INTEGER, " +
                        "PRIMARY KEY(`code`)" +
                        ")",
                "INSERT INTO Translations " +
                        "SELECT code, name, language_code, file_url, file_name, is_tafseer, is_downloaded, " +
                        "is_enabled, remote_last_update_time, local_last_update_time " +
                        "FROM Translations_old;",
                "DROP TABLE Translations_old;",

                "CREATE TABLE IF NOT EXISTS `TranslationsOrder`(" +
                        "`translation_code` TEXT NOT NULL ON CONFLICT REPLACE, " +
                        "`show_in_dialog` INTEGER, " +
                        "`order` INTEGER, " +
                        "PRIMARY KEY(`translation_code`) ON CONFLICT REPLACE " +
                        ");"
        )
        commands.forEach { database.execSQL(it) }
        database.setTransactionSuccessful()
        database.endTransaction()
    }

}