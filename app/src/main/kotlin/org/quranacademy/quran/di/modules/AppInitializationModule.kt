package org.quranacademy.quran.di.modules

import org.quranacademy.quran.appinitializer.AppInitializer
import org.quranacademy.quran.appinitializer.AppInitializersProvider
import org.quranacademy.quran.appinitializer.initializers.*
import org.quranacademy.quran.common.AppInitializersProviderImpl
import org.quranacademy.quran.common.PhilologyInitializer
import org.quranacademy.quran.di.bind
import org.quranacademy.quran.di.bindSingleton
import org.quranacademy.quran.di.toType
import toothpick.config.Module

class AppInitializationModule : Module() {

    init {
        bindSingleton<AppInitializer>()
        bind(AppInitializersProvider::class)
                .toType(AppInitializersProviderImpl::class)
                .singletonInScope()

        bindSingleton<TehreerLibraryInitializer>()
        bindSingleton<LoggingInitializer>()
        bindSingleton<AppConfigurationInitializer>()
        bindSingleton<PhilologyInitializer>()
        bindSingleton<AppMigrationInitializer>()
        bindSingleton<DatabaseInitializer>()
        bindSingleton<FileDownloaderInitializer>()
        bindSingleton<TehreerLibraryInitializer>()
    }

}
