package org.quranacademy.quran.ui.preferences.util

import android.view.View
import java.util.*

class CompositeClickListener : View.OnClickListener {

    private val listeners: MutableList<View.OnClickListener> = ArrayList()

    override fun onClick(v: View) {
        listeners.reversed().forEach { it.onClick(v) }
    }

    fun addListener(listener: View.OnClickListener): Int {
        listeners.add(listener)
        return listeners.size - 1
    }

    fun removeListener(listener: View.OnClickListener) {
        listeners.remove(listener)
    }

    fun removeListener(index: Int) {
        listeners.removeAt(index)
    }
}