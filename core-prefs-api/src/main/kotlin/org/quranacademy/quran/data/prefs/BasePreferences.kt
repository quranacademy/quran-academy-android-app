package org.quranacademy.quran.data.prefs

import android.content.Context
import android.content.SharedPreferences

open class BasePreferences(
        context: Context,
        prefsName: String = DEFAULT_SHARED_PREF_NAME
) {

    protected val sharedPreferences by lazy {
        context.getSharedPreferences(prefsName, Context.MODE_PRIVATE)
    }

    protected val preferenceUpdatesObserver by lazy {
        PreferenceUpdatesObserver(sharedPreferences)
    }

    protected open fun getString(key: String): String? {
        return sharedPreferences.getString(key, getDefaultString(key))
    }

    protected open fun putString(key: String, value: String?) {
        return sharedPreferences.edit { putString(key, value) }
    }

    protected open fun getInt(key: String): Int {
        return sharedPreferences.getInt(key, getDefaultInt(key))
    }

    protected fun putInt(key: String, value: Int) {
        sharedPreferences.edit { putInt(key, value) }
    }

    protected fun getLong(key: String): Long {
        return sharedPreferences.getLong(key, getDefaultLong(key))
    }

    protected fun putLong(key: String, value: Long) {
        sharedPreferences.edit { putLong(key, value) }
    }

    protected fun getBoolean(key: String): Boolean {
        return sharedPreferences.getBoolean(key, getDefaultBoolean(key))
    }

    protected fun putBoolean(key: String, value: Boolean) {
        sharedPreferences.edit { putBoolean(key, value) }
    }

    companion object {

        const val DEFAULT_SHARED_PREF_NAME = "quran_academy_prefs"

        val DEFAULT_VALUES = mutableMapOf<String, Any?>()

        fun getDefaultString(key: String) = DEFAULT_VALUES[key] as String?

        fun getDefaultInt(key: String) = DEFAULT_VALUES[key] as Int

        fun getDefaultLong(key: String) = DEFAULT_VALUES[key] as Long

        fun getDefaultBoolean(key: String) = DEFAULT_VALUES[key] as Boolean

    }

}

fun SharedPreferences.edit(body: SharedPreferences.Editor.() -> Unit) {
    val editor = this.edit()
    editor.body()
    editor.apply()
}