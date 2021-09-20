package org.quranacademy.quran.data.database.migrations

import com.raizlabs.android.dbflow.annotation.Migration
import com.raizlabs.android.dbflow.sql.SQLiteType
import com.raizlabs.android.dbflow.sql.migration.AlterTableMigration
import org.quranacademy.quran.data.database.AppDatabase
import org.quranacademy.quran.data.database.models.RecitationModel

@Migration(version = 7, database = AppDatabase::class, priority = 1)
class RecitationModelMigrationV7 : AlterTableMigration<RecitationModel>(RecitationModel::class.java) {

    override fun onPreMigrate() {
        addColumn(SQLiteType.TEXT, "timecode_file")
    }

}