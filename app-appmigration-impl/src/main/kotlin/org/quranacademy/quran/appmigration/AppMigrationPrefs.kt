package org.quranacademy.quran.appmigration

import android.content.Context
import org.quranacademy.quran.data.prefs.BasePreferences
import javax.inject.Inject

/**
 * Хранилище конфигурации запуска устройства
 */

class AppMigrationPrefs @Inject constructor(
        context: Context
) : BasePreferences(context) {

    /**
     * Метод, возвращающий последнюю запущенную текущую версию приложения
     *
     * @return - последняя версия
     */
    fun getLastLaunchVersion(): Int {
        return getInt(LAST_LAUNCH_VERSION)
    }

    /**
     * Метод, устанавливающий последнюю запущенную текущую версию приложения
     *
     * @param lastLaunchVersion - последняя версия
     */
    fun setLastLaunchVersion(lastLaunchVersion: Int) {
        putInt(LAST_LAUNCH_VERSION, lastLaunchVersion)
    }

    companion object {
        const val LAST_LAUNCH_VERSION = "last_launch_version"

        const val LAST_LAUNCH_VERSION_DEFAULT = -1

        init {
            DEFAULT_VALUES[LAST_LAUNCH_VERSION] = LAST_LAUNCH_VERSION_DEFAULT
        }
    }

}