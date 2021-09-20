package org.quranacademy.quran.data.database

import android.database.Cursor
import com.raizlabs.android.dbflow.config.FlowManager
import com.raizlabs.android.dbflow.structure.database.FlowCursor

inline fun <reified T> Cursor.fillModels(): List<T> {
    val adapter by lazy {
        FlowManager.getModelAdapter(T::class.java)
    }
    return (1..count).map {
        moveToNext()
        val model = T::class.java.newInstance()
        adapter.loadFromCursor(FlowCursor.from(this), model)
        model
    }.also { close() }
}

inline fun <reified T> Cursor.fillModel(): T {
    val adapter by lazy {
        FlowManager.getModelAdapter(T::class.java)
    }
    moveToNext()
    val model = T::class.java.newInstance()
    adapter.loadFromCursor(FlowCursor.from(this), model)
    return model.also { close() }
}