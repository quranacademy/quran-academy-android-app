package org.quranacademy.quran.data.prefs

import android.content.Context
import javax.inject.Inject

class GeneralPreferences @Inject constructor(context: Context) {

    protected val sharedPreferences by lazy {
        context.getSharedPreferences(BasePreferences.DEFAULT_SHARED_PREF_NAME, Context.MODE_PRIVATE)
    }

    fun getString(key: String, defValue: String?): String? {
        return sharedPreferences.getString(key, defValue)
    }

    fun putString(key: String, value: String?) {
        return sharedPreferences.edit { putString(key, value) }
    }

    fun getInt(key: String, defValue: Int): Int {
        return sharedPreferences.getInt(key, defValue)
    }

    fun putInt(key: String, value: Int) {
        sharedPreferences.edit { putInt(key, value) }
    }

    fun getLong(key: String, defValue: Long): Long {
        return sharedPreferences.getLong(key, defValue)
    }

    fun putLong(key: String, value: Long) {
        sharedPreferences.edit { putLong(key, value) }
    }

    fun getBoolean(key: String, defValue: Boolean): Boolean {
        return sharedPreferences.getBoolean(key, defValue)
    }

    fun putBoolean(key: String, value: Boolean) {
        sharedPreferences.edit { putBoolean(key, value) }
    }

}