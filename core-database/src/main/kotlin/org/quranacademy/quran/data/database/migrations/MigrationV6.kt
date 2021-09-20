package org.quranacademy.quran.data.database.migrations

import com.raizlabs.android.dbflow.annotation.Migration
import com.raizlabs.android.dbflow.sql.migration.BaseMigration
import com.raizlabs.android.dbflow.structure.database.DatabaseWrapper
import org.quranacademy.quran.data.database.AppDatabase

@Migration(version = 6, database = AppDatabase::class)
class MigrationV6 : BaseMigration() {

    override fun migrate(database: DatabaseWrapper) {
        database.beginTransaction()
        val commands = listOf(
                "CREATE TABLE IF NOT EXISTS `RecentReadingPlaces`(" +
                        "`id` INTEGER, " +
                        "`surah` INTEGER, " +
                        "`ayah` INTEGER, " +
                        "`is_mushaf_mode` INTEGER, " +
                        "`timestamp` INTEGER, " +
                        "PRIMARY KEY(`id`)" +
                        ")"
        )
        commands.forEach { database.execSQL(it) }
        database.setTransactionSuccessful()
        database.endTransaction()
    }

}