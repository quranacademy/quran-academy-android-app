package org.quranacademy.quran.appmigration

import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import org.quranacademy.quran.appmigration.di.AppMigrationsProvider
import timber.log.Timber
import javax.inject.Inject

/**
 * Осуществляет миграцию приложения на новую версию
 */
class AppMigrationManagerImpl @Inject constructor(
        private val appMigrationPrefs: AppMigrationPrefs,
        private val appMigrationsProvider: AppMigrationsProvider
) : AppMigrationManager {

    private val migrationFinished = ConflatedBroadcastChannel(false)

    override fun isMigrationNeeded(): Boolean {
        return appMigrationPrefs.getLastLaunchVersion() == CURRENT_VERSION
    }

    override suspend fun tryMigrateApp() {
        val lastLaunchVersion = appMigrationPrefs.getLastLaunchVersion()
        if (lastLaunchVersion != CURRENT_VERSION) {
            Timber.i("Migration started from $lastLaunchVersion to $CURRENT_VERSION")
            for (migration in appMigrationsProvider.getMigrations()) {
                try {
                    migration.execute(lastLaunchVersion, CURRENT_VERSION)
                } catch (e: Exception) {
                    //Перехватываем все исключения, т. к. неизвестно какая реализация у метода execute
                    Timber.e(e, "Migration with baseVersion ${migration.baseVersion} failed")
                }
            }
        }
        appMigrationPrefs.setLastLaunchVersion(CURRENT_VERSION)
        migrationFinished.send(true)
    }

    override fun subscribeOnMigrationFinished(): Flow<Boolean> = migrationFinished.asFlow()

    companion object {
        const val CURRENT_VERSION = 5
    }

}