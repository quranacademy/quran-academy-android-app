package org.quranacademy.quran.appmigration.migrations

import org.quranacademy.quran.appmigration.AppMigration
import timber.log.Timber
import javax.inject.Inject

class MigrationForEach @Inject constructor(

) : AppMigration(-1) {

    override fun execute(oldVer: Int, newVer: Int) {
        Timber.i("Executing app migration for each update")
        apply() //нет проверки версий
    }

    override fun apply() {
        //здесь должен быть код миграции, который будет вызываться для каждого обновления
    }

}