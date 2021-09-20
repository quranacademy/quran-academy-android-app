package org.quranacademy.quran.appmigration

import timber.log.Timber

/**
 * Базовый класс миграции с одной версии приложения на другую
 */
abstract class AppMigration(val baseVersion: Int) {

    open fun execute(oldVer: Int, newVer: Int) {
        if (baseVersion in (oldVer + 1)..newVer) {
            Timber.i("Executing app migration, baseVersion = $baseVersion")
            apply()
        }
    }

    protected abstract fun apply()

}