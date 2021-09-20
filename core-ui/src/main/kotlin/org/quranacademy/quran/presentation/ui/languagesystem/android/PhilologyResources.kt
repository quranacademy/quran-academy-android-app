package org.quranacademy.quran.presentation.ui.languagesystem.android

import android.content.res.Resources

class PhilologyResources(
        baseResources: Resources
) : Resources(baseResources.assets, baseResources.displayMetrics, baseResources.configuration) {

    private val resourcesDelegate = ResourcesDelegate(baseResources)

    override fun getText(id: Int): CharSequence = resourcesDelegate.getText(id)

    override fun getString(id: Int): String = resourcesDelegate.getString(id)

    override fun getStringArray(id: Int): Array<String> = resourcesDelegate.getStringArray(id)

    override fun getQuantityString(
            id: Int, quantity: Int, vararg formatArgs: Any
    ): String = resourcesDelegate.getQuantityString(id, quantity, quantity)

    override fun getQuantityString(id: Int, quantity: Int): String = resourcesDelegate.getQuantityString(id, quantity)

}
