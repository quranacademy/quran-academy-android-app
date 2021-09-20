package org.quranacademy.quran.settings.presentation.ui

import android.content.Context
import android.os.Bundle
import org.quranacademy.quran.data.prefs.BasePreferences
import org.quranacademy.quran.data.prefs.edit
import org.quranacademy.quran.ui.preferences.io.StorageModule

class PreferencesStorageModule(context: Context) : StorageModule {

    private val sharedPreferences by lazy {
        context.getSharedPreferences(BasePreferences.DEFAULT_SHARED_PREF_NAME, Context.MODE_PRIVATE)
    }

    override fun getBoolean(key: String, defaultVal: Boolean): Boolean {
        return sharedPreferences.getBoolean(key, BasePreferences.getDefaultBoolean(key))
    }

    override fun saveBoolean(key: String, value: Boolean) {
        sharedPreferences.edit { putBoolean(key, value) }
    }

    override fun getInt(key: String, defaultVal: Int): Int {
        return sharedPreferences.getInt(key, BasePreferences.getDefaultInt(key))
    }

    override fun saveInt(key: String, value: Int) {
        sharedPreferences.edit { putInt(key, value) }
    }

    override fun getString(key: String, defaultVal: String): String {
        return sharedPreferences.getString(key, BasePreferences.getDefaultString(key))!!
    }

    override fun saveString(key: String, value: String) {
        sharedPreferences.edit { putString(key, value) }
    }

    override fun getStringSet(key: String, defaultVal: Set<String>): MutableSet<String> {
        return sharedPreferences.getStringSet(key, defaultVal)!!
    }

    override fun saveStringSet(key: String, value: Set<String>) {
        sharedPreferences.edit { putStringSet(key, value) }
    }

    override fun onRestoreInstanceState(savedState: Bundle) {

    }

    override fun onSaveInstanceState(outState: Bundle) {

    }

}