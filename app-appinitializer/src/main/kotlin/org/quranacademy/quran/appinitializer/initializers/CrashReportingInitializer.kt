package org.quranacademy.quran.appinitializer.initializers

import android.annotation.SuppressLint
import android.content.Context
import com.google.firebase.crashlytics.FirebaseCrashlytics
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.quranacademy.quran.appinitializer.AppInitializerElement
import org.quranacademy.quran.domain.models.AppInfo
import javax.inject.Inject

class CrashReportingInitializer @Inject constructor(
        private val appInfo: AppInfo
) : AppInitializerElement {

    private val crashlytics by lazy {
        FirebaseCrashlytics.getInstance()
    }

    @SuppressLint("HardwareIds")
    override fun initialize(context: Context) {
        crashlytics.setCustomKey("BUILD_NUMBER", appInfo.versionCode)
        crashlytics.setCustomKey("BUILD_TYPE", appInfo.buildType)
        crashlytics.setCustomKey("BUILD_UID", appInfo.buildId)
        crashlytics.setCustomKey("BUILD_TIME", appInfo.buildTime)

        GlobalScope.launch {
            try {
                crashlytics.sendUnsentReports()
            } catch (e: Throwable) {

            }
        }
    }

}