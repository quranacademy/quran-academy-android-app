package org.quranacademy.quran.appinitializer.initializers

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.quranacademy.quran.appinitializer.AppInitializerElement
import org.quranacademy.quran.appmigration.AppMigrationManager
import javax.inject.Inject

class AppMigrationInitializer @Inject constructor(
        private val appMigrationManager: AppMigrationManager
) : AppInitializerElement {

    override fun initialize(context: Context) {
        GlobalScope.launch(context = Dispatchers.IO) {
            appMigrationManager.tryMigrateApp()
        }
    }

}