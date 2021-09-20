package org.quranacademy.quran.data.prefs

import android.content.SharedPreferences
import android.content.SharedPreferences.OnSharedPreferenceChangeListener
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class PreferenceUpdatesObserver(
        private val preferences: SharedPreferences
) {

    private val prefUpdates = BroadcastChannel<String>(1)

    init {
        val listener = OnSharedPreferenceChangeListener { preferences, key ->
            GlobalScope.launch { prefUpdates.send(key) }
        }
        prefUpdates.invokeOnClose {
            preferences.registerOnSharedPreferenceChangeListener(listener)
        }
        preferences.registerOnSharedPreferenceChangeListener(listener)
    }

    fun observeString(key: String): Flow<String> {
        return observePref(key, PrefType.STRING)
    }

    fun observeInt(key: String): Flow<Int> {
        return observePref(key, PrefType.INT)
    }

    fun observeLong(key: String): Flow<Long> {
        return observePref(key, PrefType.LONG)
    }

    fun observeFloat(key: String): Flow<Float> {
        return observePref(key, PrefType.FLOAT)
    }

    fun observeBoolean(key: String): Flow<Boolean> {
        return observePref(key, PrefType.BOOLEAN)
    }

    private inline fun <reified T> observePref(key: String, type: PrefType) = prefUpdates.asFlow()
            .filter { it == key }
            .map { getValue<T>(key, type) }

    private inline fun <reified T> getValue(key: String, type: PrefType): T {
        return when (type) {
            PrefType.STRING -> preferences.getString(key, DEFAULT_STRING) as T
            PrefType.INT -> preferences.getInt(key, DEFAULT_INTEGER) as T
            PrefType.FLOAT -> preferences.getLong(key, DEFAULT_LONG) as T
            PrefType.LONG -> preferences.getFloat(key, DEFAULT_FLOAT) as T
            PrefType.BOOLEAN -> preferences.getBoolean(key, DEFAULT_BOOLEAN) as T
        }
    }

    companion object {
        private const val DEFAULT_STRING = ""
        private const val DEFAULT_INTEGER = 0
        private const val DEFAULT_LONG = 0L
        private const val DEFAULT_FLOAT = 0f
        private const val DEFAULT_BOOLEAN = false
    }

    private enum class PrefType {
        STRING, INT, LONG, FLOAT, BOOLEAN
    }

}