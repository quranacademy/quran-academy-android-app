package org.quranacademy.quran.data.database.migrations

import com.raizlabs.android.dbflow.annotation.Migration
import com.raizlabs.android.dbflow.sql.migration.BaseMigration
import com.raizlabs.android.dbflow.structure.database.DatabaseWrapper
import org.quranacademy.quran.data.database.AppDatabase

@Migration(version = 2, database = AppDatabase::class)
class MigrationV2 : BaseMigration() {

    override fun migrate(database: DatabaseWrapper) {
        database.execSQL(
                "CREATE TABLE IF NOT EXISTS `Languages`(" +
                        "`id` INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "`code` TEXT, " +
                        "`name` TEXT, " +
                        "`is_rtl` INTEGER, " +
                        "`is_downloaded` INTEGER" +
                        ")"
        )
    }

}