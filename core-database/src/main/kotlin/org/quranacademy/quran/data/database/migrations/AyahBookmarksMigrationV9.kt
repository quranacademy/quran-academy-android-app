package org.quranacademy.quran.data.database.migrations

import com.raizlabs.android.dbflow.annotation.Migration
import com.raizlabs.android.dbflow.sql.SQLiteType
import com.raizlabs.android.dbflow.sql.migration.AlterTableMigration
import org.quranacademy.quran.data.database.AppDatabase
import org.quranacademy.quran.data.database.models.AyahBookmarkModel

@Migration(version = 9, database = AppDatabase::class, priority = 1)
class AyahBookmarksMigrationV9 : AlterTableMigration<AyahBookmarkModel>(AyahBookmarkModel::class.java) {

    override fun onPreMigrate() {
        addForeignKeyColumn(SQLiteType.INTEGER, "folder_id", "AyahBookmarks(id) ON DELETE CASCADE")
    }

}