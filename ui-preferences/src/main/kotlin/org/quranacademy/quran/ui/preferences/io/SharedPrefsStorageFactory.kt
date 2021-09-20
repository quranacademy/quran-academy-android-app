package org.quranacademy.quran.ui.preferences.io

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import androidx.annotation.Nullable

class SharedPrefsStorageFactory(@param:Nullable private val preferencesName: String?) : StorageModule.Factory {

    override fun create(context: Context): StorageModule {
        val prefs: SharedPreferences = if (preferencesName != null) {
            context.getSharedPreferences(preferencesName, Context.MODE_PRIVATE)
        } else {
            PreferenceManager.getDefaultSharedPreferences(context)
        }
        return SharedPreferenceStorageModule(prefs)
    }
}
