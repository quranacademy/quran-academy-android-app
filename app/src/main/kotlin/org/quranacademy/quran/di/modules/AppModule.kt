package org.quranacademy.quran.di.modules

import org.quranacademy.quran.BuildConfig
import org.quranacademy.quran.RadioPlayerSynchronizer
import org.quranacademy.quran.appmigration.AppMigrationManager
import org.quranacademy.quran.appmigration.AppMigrationManagerImpl
import org.quranacademy.quran.common.RadioPlayerSynchonizerImpl
import org.quranacademy.quran.data.PathProvider
import org.quranacademy.quran.data.PathProviderImpl
import org.quranacademy.quran.di.bind
import org.quranacademy.quran.di.bindSingleton
import org.quranacademy.quran.di.toType
import org.quranacademy.quran.domain.commons.ResourcesManager
import org.quranacademy.quran.domain.commons.SystemManager
import org.quranacademy.quran.sharingdialog.sharingmanager.AyahsTextSharingManager
import org.quranacademy.quran.domain.models.AppInfo
import org.quranacademy.quran.presentation.AppearanceManagerImpl
import org.quranacademy.quran.presentation.LanguageManagerImpl
import org.quranacademy.quran.presentation.ResourcesManagerImpl
import org.quranacademy.quran.presentation.mvp.ErrorHandler
import org.quranacademy.quran.presentation.systemmanager.AndroidSystemManager
import org.quranacademy.quran.presentation.ui.appearance.AppearanceManager
import org.quranacademy.quran.presentation.ui.appearance.LanguageManager
import toothpick.config.Module

class AppModule : Module() {

    init {
        bind(AppMigrationManager::class).toType(AppMigrationManagerImpl::class).singletonInScope()
        bind(ResourcesManager::class).toType(ResourcesManagerImpl::class).singletonInScope()
        bind(AppearanceManager::class).toType(AppearanceManagerImpl::class).singletonInScope()
        bind(LanguageManager::class).toType(LanguageManagerImpl::class).singletonInScope()
        bind(AyahsTextSharingManager::class).singletonInScope()
        bindSingleton<ErrorHandler>()

        bind(PathProvider::class)
                .toType(PathProviderImpl::class)
                .singletonInScope()
        bind(RadioPlayerSynchronizer::class)
                .toType(RadioPlayerSynchonizerImpl::class)
                .singletonInScope()

        val androidSystemManager = AndroidSystemManager()
        bind(AndroidSystemManager::class).toInstance(androidSystemManager)
        bind(SystemManager::class).toInstance(androidSystemManager)

        val appInfo = AppInfo(
                applicationId = BuildConfig.APPLICATION_ID,
                versionName = BuildConfig.VERSION_NAME,
                versionCode = BuildConfig.VERSION_CODE,
                buildId = BuildConfig.BUILD_UID.take(8),
                buildType = BuildConfig.BUILD_TYPE,
                buildTime = BuildConfig.BUILD_TIME,
                isDebug = BuildConfig.DEBUG
        )
        bind(AppInfo::class).toInstance(appInfo)
    }

}