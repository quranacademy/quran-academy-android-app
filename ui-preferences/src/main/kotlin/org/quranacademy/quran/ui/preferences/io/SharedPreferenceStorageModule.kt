package org.quranacademy.quran.ui.preferences.io

import android.content.SharedPreferences
import android.os.Bundle

class SharedPreferenceStorageModule(private val prefs: SharedPreferences) : StorageModule {

    override fun saveBoolean(key: String, value: Boolean) {
        prefs.edit().putBoolean(key, value).apply()
    }

    override fun saveString(key: String, value: String) {
        prefs.edit().putString(key, value).apply()
    }

    override fun saveInt(key: String, value: Int) {
        prefs.edit().putInt(key, value).apply()
    }

    override fun saveStringSet(key: String, value: Set<String>) {
        prefs.edit().putStringSet(key, value).apply()
    }

    override fun getBoolean(key: String, defaultVal: Boolean): Boolean {
        return prefs.getBoolean(key, defaultVal)
    }

    override fun getString(key: String, defaultVal: String): String {
        return prefs.getString(key, defaultVal)!!
    }

    override fun getInt(key: String, defaultVal: Int): Int {
        return prefs.getInt(key, defaultVal)
    }

    override fun getStringSet(key: String, defaultVal: Set<String>): Set<String> {
        return prefs.getStringSet(key, defaultVal)!!
    }

    override fun onSaveInstanceState(outState: Bundle) {

    }

    override fun onRestoreInstanceState(savedState: Bundle) {

    }
}
