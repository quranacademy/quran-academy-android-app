package org.quranacademy.quran.common

import org.quranacademy.quran.appinitializer.AppInitializerElement
import org.quranacademy.quran.appinitializer.AppInitializersProvider
import org.quranacademy.quran.appinitializer.initializers.*
import org.quranacademy.quran.di.DI
import org.quranacademy.quran.di.get
import toothpick.Toothpick
import javax.inject.Inject

class AppInitializersProviderImpl @Inject constructor() : AppInitializersProvider {

    override fun getInitializers(): List<AppInitializerElement> {
        val appScope = Toothpick.openScope(DI.APP_SCOPE)
        return listOf(
                appScope.get<CrashReportingInitializer>(),
                appScope.get<LoggingInitializer>(),
                appScope.get<AppConfigurationInitializer>(),
                appScope.get<PhilologyInitializer>(),
                appScope.get<DatabaseInitializer>(),
                appScope.get<AppMigrationInitializer>(),
                appScope.get<FileDownloaderInitializer>(),
                appScope.get<TehreerLibraryInitializer>()
        )
    }

}