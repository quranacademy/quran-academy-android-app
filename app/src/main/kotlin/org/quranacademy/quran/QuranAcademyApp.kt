package org.quranacademy.quran

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import androidx.multidex.MultiDex
import com.arellomobile.mvp.RegisterMoxyReflectorPackages
import org.quranacademy.quran.appinitializer.AppInitializer
import org.quranacademy.quran.appmigration.di.AppMigrationManagerModule
import org.quranacademy.quran.data.network.di.NetworkModule
import org.quranacademy.quran.data.prefs.di.PreferencesModule
import org.quranacademy.quran.di.CoreModule
import org.quranacademy.quran.di.DI
import org.quranacademy.quran.di.getGlobal
import org.quranacademy.quran.di.modules.*
import org.quranacademy.quran.presentation.ui.languagesystem.Philology
import toothpick.Toothpick
import toothpick.configuration.Configuration
import java.util.*

@SuppressLint("Registered")
@RegisterMoxyReflectorPackages(
        "org.quranacademy.quran.ayahdetails",
        "org.quranacademy.quran.audiomanager",
        "org.quranacademy.quran.bookmarks",
        "org.quranacademy.quran.feedback",
        "org.quranacademy.quran.languagesmanager",
        "org.quranacademy.quran.mainscreen",
        "org.quranacademy.quran.memorization",
        "org.quranacademy.quran.mushaf",
        "org.quranacademy.quran.player",
        "org.quranacademy.quran.radio",
        "org.quranacademy.quran.search",
        "org.quranacademy.quran.settings",
        "org.quranacademy.quran.sharingdialog",
        "org.quranacademy.quran.splash",
        "org.quranacademy.quran.surahdetails",
        "org.quranacademy.quran.textsettingspanel",
        "org.quranacademy.quran.tajweedrules",
        "org.quranacademy.quran.translationsmanager",
        "org.quranacademy.quran.updateneeded",
        "org.quranacademy.quran.wordbywordmanager"
)
open class QuranAcademyApp : Application(), App {

    private val appLaunchCode = UUID.randomUUID().toString()

    override fun onCreate() {
        super.onCreate()

        initToothpick()
        getGlobal<AppInitializer>().initialize()
    }

    private fun initToothpick() {
        if (BuildConfig.DEBUG) {
            Toothpick.setConfiguration(Configuration.forDevelopment().preventMultipleRootScopes())
        } else {
            Toothpick.setConfiguration(Configuration.forProduction())
        }

        Toothpick.openScope(DI.APP_SCOPE)
                .installModules(
                        ContextModule(this),
                        AppInitializationModule(),
                        AppMigrationManagerModule(),
                        AppModule(),
                        CoreModule(),
                        DataModule(this),
                        DaosModule(),
                        NavigationModule(),
                        NetworkModule(),
                        PlayerAndRadioModule(),
                        PreferencesModule(),
                        RepositoriesModule()
                )
    }

    override fun attachBaseContext(base: Context) {
        Philology.setOriginalAppContext(base)
        super.attachBaseContext(base)

        MultiDex.install(this)
    }

    override fun getAppLaunchCode(): String = appLaunchCode

}
