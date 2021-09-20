package org.quranacademy.quran.data.database.migrations

import com.raizlabs.android.dbflow.annotation.Migration
import com.raizlabs.android.dbflow.sql.migration.BaseMigration
import com.raizlabs.android.dbflow.structure.database.DatabaseWrapper
import org.quranacademy.quran.data.database.AppDatabase

@Migration(version = 4, database = AppDatabase::class)
class MigrationV4 : BaseMigration() {

    override fun migrate(database: DatabaseWrapper) {
        database.execSQL(
                "CREATE TABLE IF NOT EXISTS `recitations`(" +
                        "`id` INTEGER, " +
                        "`name` INTEGER, " +
                        "`audio_url_pattern` INTEGER, " +
                        "PRIMARY KEY(`id`)" +
                        ")"
        )
    }

}