package org.quranacademy

import com.facebook.stetho.Stetho
import org.quranacademy.quran.QuranAcademyApp

class DebugQuranAcademyApp : QuranAcademyApp() {

    override fun onCreate() {
        super.onCreate()

        initDatabaseDebugTools()
    }

    private fun initDatabaseDebugTools() {
        Stetho.initializeWithDefaults(this)
    }

}