package org.quranacademy.quran.appmigration

import kotlinx.coroutines.flow.Flow

interface AppMigrationManager {

    fun isMigrationNeeded(): Boolean

    fun subscribeOnMigrationFinished(): Flow<Boolean>

    suspend fun tryMigrateApp()

}