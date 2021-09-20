package org.quranacademy.quran.ui.preferences.io

import android.content.Context
import android.os.Bundle

interface StorageModule {

    fun saveBoolean(key: String, value: Boolean)

    fun saveString(key: String, value: String)

    fun saveInt(key: String, value: Int)

    fun saveStringSet(key: String, value: Set<String>)

    fun getBoolean(key: String, defaultVal: Boolean): Boolean

    fun getString(key: String, defaultVal: String): String

    fun getInt(key: String, defaultVal: Int): Int

    fun getStringSet(key: String, defaultVal: Set<String>): Set<String>

    fun onSaveInstanceState(outState: Bundle)

    fun onRestoreInstanceState(savedState: Bundle)

    interface Factory {
        fun create(context: Context): StorageModule
    }
}
