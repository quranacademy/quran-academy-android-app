package org.quranacademy.quran.data.database.migrations

import com.raizlabs.android.dbflow.annotation.Migration
import com.raizlabs.android.dbflow.sql.migration.BaseMigration
import com.raizlabs.android.dbflow.structure.database.DatabaseWrapper
import org.quranacademy.quran.data.database.AppDatabase

@Migration(version = 9, database = AppDatabase::class)
class MigrationV9 : BaseMigration() {

    override fun migrate(database: DatabaseWrapper) {
        database.beginTransaction()
        val createTableSql = "CREATE TABLE IF NOT EXISTS `BookmarkFolders`(" +
                "`id` INTEGER, " +
                "`name` TEXT, " +
                "`timestamp` INTEGER, " +
                "PRIMARY KEY(`id`)" +
                ")"
        database.execSQL(createTableSql)
        database.setTransactionSuccessful()
        database.endTransaction()
    }

}