package org.quranacademy.quran.player.presentation.global.extensions

import android.os.Bundle
import androidx.fragment.app.Fragment
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

inline fun <reified T> argument(
        key: String,
        defaultValue: T? = null
): ReadWriteProperty<Fragment, T> =
        BundleExtractorDelegate { thisRef ->
            extractFromBundle(
                    bundle = thisRef.arguments,
                    key = key,
                    defaultValue = defaultValue
            )
        }

inline fun <reified T> extractFromBundle(
        bundle: Bundle?,
        key: String,
        defaultValue: T? = null
): T {
    val result = bundle?.get(key) ?: defaultValue
    if (result != null && result !is T) {
        throw ClassCastException("Property $key has different class type")
    }
    return result as T
}

class BundleExtractorDelegate<R, T>(private val initializer: (R) -> T) : ReadWriteProperty<R, T> {

    private object EMPTY

    private var value: Any? = EMPTY

    override fun setValue(thisRef: R, property: KProperty<*>, value: T) {
        this.value = value
    }

    override fun getValue(thisRef: R, property: KProperty<*>): T {
        if (value == EMPTY) {
            value = initializer(thisRef)
        }
        @Suppress("UNCHECKED_CAST")
        return value as T
    }

}
