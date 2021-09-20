package org.quranacademy.quran.data.database.migrations

import com.raizlabs.android.dbflow.annotation.Migration
import com.raizlabs.android.dbflow.sql.SQLiteType
import com.raizlabs.android.dbflow.sql.migration.AlterTableMigration
import org.quranacademy.quran.data.database.AppDatabase
import org.quranacademy.quran.data.database.models.WordByWordTranslationModel

@Migration(version = 3, database = AppDatabase::class, priority = 1)
class WordByWordTranslationModelMigrationV3 : AlterTableMigration<WordByWordTranslationModel>(WordByWordTranslationModel::class.java) {

    override fun onPreMigrate() {
        addColumn(SQLiteType.INTEGER, "remote_last_update_time")
        addColumn(SQLiteType.INTEGER, "local_last_update_time")
    }

}