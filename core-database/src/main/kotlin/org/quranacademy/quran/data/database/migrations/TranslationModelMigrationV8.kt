package org.quranacademy.quran.data.database.migrations

import com.raizlabs.android.dbflow.annotation.Migration
import com.raizlabs.android.dbflow.sql.SQLiteType
import com.raizlabs.android.dbflow.sql.migration.AlterTableMigration
import org.quranacademy.quran.data.database.AppDatabase
import org.quranacademy.quran.data.database.models.TranslationModel

@Migration(version = 8, database = AppDatabase::class, priority = 1)
class TranslationModelMigrationV8 : AlterTableMigration<TranslationModel>(TranslationModel::class.java) {

    override fun onPreMigrate() {
        addColumn(SQLiteType.INTEGER, "file_size")
    }

}