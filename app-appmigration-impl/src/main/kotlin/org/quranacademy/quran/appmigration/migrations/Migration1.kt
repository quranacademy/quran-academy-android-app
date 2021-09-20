package org.quranacademy.quran.appmigration.migrations

import org.quranacademy.quran.appmigration.AppMigration
import org.quranacademy.quran.data.prefs.AppearancePreferences
import javax.inject.Inject

class Migration1 @Inject constructor(
        private val appearancePreferences: AppearancePreferences
) : AppMigration(1) {

    override fun apply() {
        migrateMushafTheme()
    }

    private fun migrateMushafTheme() {
        val appTheme = appearancePreferences.getCurrentAppTheme()
        appearancePreferences.setCurrentMushafTheme(appTheme)
    }

}