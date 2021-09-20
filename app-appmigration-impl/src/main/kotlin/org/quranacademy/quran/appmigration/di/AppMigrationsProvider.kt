package org.quranacademy.quran.appmigration.di

import org.quranacademy.quran.appmigration.AppMigration
import org.quranacademy.quran.appmigration.migrations.*
import org.quranacademy.quran.di.DI
import toothpick.Toothpick
import javax.inject.Inject

class AppMigrationsProvider @Inject constructor() {

    fun getMigrations(): List<AppMigration> {
        val appScope = Toothpick.openScope(DI.APP_SCOPE)
        return listOf(
                appScope.getInstance(MigrationForEach::class.java),
                appScope.getInstance(Migration1::class.java),
                appScope.getInstance(Migration2::class.java),
                appScope.getInstance(Migration3::class.java),
                appScope.getInstance(Migration4::class.java),
                appScope.getInstance(Migration5::class.java)
        )
    }

}