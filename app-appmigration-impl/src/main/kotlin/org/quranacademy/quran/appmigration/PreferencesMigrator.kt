package org.quranacademy.quran.appmigration

import android.content.Context
import org.quranacademy.quran.data.prefs.BasePreferences
import org.quranacademy.quran.data.prefs.edit
import javax.inject.Inject

/**
 * Вспомогательный класс для удобной миграции SharedPreferences: удаления, переименования полей
 */
class PreferencesMigrator @Inject constructor(
        context: Context
) : BasePreferences(context) {

    fun contains(key: String): Boolean = sharedPreferences.contains(key)

    fun getInt(key: String, defValue: Int): Int = sharedPreferences.getInt(key, defValue)

    fun getLong(key: String, defValue: Long): Long = sharedPreferences.getLong(key, defValue)

    fun getBoolean(key: String, defValue: Boolean) = sharedPreferences.getBoolean(key, defValue)

    fun setBoolean(key: String, value: Boolean) = putBoolean(key, value)

    fun renameBoolean(oldKey: String, newKey: String) {
        if (sharedPreferences.contains(oldKey)) {
            val value = sharedPreferences.getBoolean(oldKey, getDefaultBoolean(newKey))
            sharedPreferences.edit {
                remove(oldKey)
                putBoolean(newKey, value)
            }
        }
    }

    fun removeField(key: String) {
        sharedPreferences.edit().remove(key).apply()
    }

}