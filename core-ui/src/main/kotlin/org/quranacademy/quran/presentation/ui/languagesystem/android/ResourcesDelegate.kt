package org.quranacademy.quran.presentation.ui.languagesystem.android

import android.content.res.Resources
import org.quranacademy.quran.presentation.ui.languagesystem.Philology

internal class ResourcesDelegate(
        private val baseResources: Resources
) {

    @Throws(Resources.NotFoundException::class)
    fun getText(id: Int): String {
        return getString(id)
                .replace("<![CDATA[", "")
                .replace("]]>", "")
    }

    @Throws(Resources.NotFoundException::class)
    fun getString(id: Int): String {
        val repository = Philology.getCurrentPhilologyRepository()
        return repository.getString(getResName(id)) ?: baseResources.getString(id)
    }

    @Throws(Resources.NotFoundException::class)
    fun getStringArray(id: Int): Array<String> {
        val originalArray = baseResources.getStringArray(id)
        return originalArray
                .map {
                    //If at least one element is null, we return the original array.
                    getString(Philology.getStringId(it) ?: return originalArray)
                }
                .toTypedArray()

    }

    @Throws(Resources.NotFoundException::class)
    fun getQuantityString(id: Int, quantity: Int, vararg formatArgs: Any): String {
        val raw = getQuantityString(id, quantity)
        return String.format(raw, *formatArgs)
    }

    @Throws(Resources.NotFoundException::class)
    fun getQuantityString(id: Int, quantity: Int): String {
        val repository = Philology.getCurrentPhilologyRepository()
        return repository.getQuantityString(getResName(id), quantity)
                ?: baseResources.getQuantityString(id, quantity)
    }

    private fun getResName(id: Int): String = baseResources.getResourceEntryName(id)

}

