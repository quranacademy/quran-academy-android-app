package org.quranacademy.quran.data.database.migrations

import com.raizlabs.android.dbflow.annotation.Migration
import com.raizlabs.android.dbflow.sql.migration.BaseMigration
import com.raizlabs.android.dbflow.structure.database.DatabaseWrapper
import org.quranacademy.quran.data.database.AppDatabase

@Migration(version = 3, database = AppDatabase::class)
class MigrationV3 : BaseMigration() {

    override fun migrate(database: DatabaseWrapper) {}

}