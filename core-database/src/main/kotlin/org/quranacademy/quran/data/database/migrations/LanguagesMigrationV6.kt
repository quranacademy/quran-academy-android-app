package org.quranacademy.quran.data.database.migrations

import com.raizlabs.android.dbflow.annotation.Migration
import com.raizlabs.android.dbflow.sql.SQLiteType
import com.raizlabs.android.dbflow.sql.migration.AlterTableMigration
import org.quranacademy.quran.data.database.AppDatabase
import org.quranacademy.quran.data.database.models.LanguageModel

@Migration(version = 6, database = AppDatabase::class, priority = 1)
class LanguagesMigrationV6 : AlterTableMigration<LanguageModel>(LanguageModel::class.java) {

    override fun onPreMigrate() {
        addColumn(SQLiteType.TEXT, "english_name")
    }

}