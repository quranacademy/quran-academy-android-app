package org.quranacademy.quran.appmigration.di

import org.quranacademy.quran.appmigration.AppMigrationManager
import org.quranacademy.quran.appmigration.AppMigrationManagerImpl
import org.quranacademy.quran.appmigration.AppMigrationPrefs
import org.quranacademy.quran.appmigration.PreferencesMigrator
import org.quranacademy.quran.appmigration.migrations.MigrationForEach
import toothpick.config.Module

class AppMigrationManagerModule : Module() {

    init {
        bind(MigrationForEach::class.java).singletonInScope()
        bind(AppMigrationsProvider::class.java).singletonInScope()
        bind(AppMigrationPrefs::class.java).singletonInScope()
        bind(PreferencesMigrator::class.java).singletonInScope()
        bind(AppMigrationManager::class.java).to(AppMigrationManagerImpl::class.java).singletonInScope()
    }

}