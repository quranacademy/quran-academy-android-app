package org.quranacademy.quran.appinitializer.initializers

import android.content.Context
import com.orhanobut.logger.AndroidLogAdapter
import com.orhanobut.logger.Logger
import com.orhanobut.logger.PrettyFormatStrategy
import org.quranacademy.quran.appinitializer.AppInitializerElement
import org.quranacademy.quran.domain.models.AppInfo
import org.quranacademy.quran.reporting.CrashlyticsTree
import timber.log.Timber
import javax.inject.Inject

class LoggingInitializer @Inject constructor(
        private val appInfo: AppInfo
) : AppInitializerElement {

    override fun initialize(context: Context) {
        val formatStrategy = PrettyFormatStrategy.newBuilder()
                .showThreadInfo(false)  // (Optional) Whether to show thread info or not. Default true
                .methodCount(3)         // (Optional) How many method line to show. Default 2
                .methodOffset(5)        // (Optional) Hides internal method calls up to offset. Default 5
                .tag("QuranAcademy")   // (Optional) Global tag for every log. Default PRETTY_LOGGER
                .build()

        Logger.addLogAdapter(AndroidLogAdapter(formatStrategy))

        if (appInfo.isDebug) {
            Timber.plant(object : Timber.DebugTree() {
                override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
                    Logger.log(priority, tag, message, t)
                }
            })
            Timber.tag("QuranAcademy")
        } else {
            Timber.plant(CrashlyticsTree())
        }
    }

}