package org.quranacademy.quran.presentation.ui.languagesystem.android

import android.content.Context
import android.content.ContextWrapper
import android.content.res.Resources

class PhilologyContextWrapper(
        base: Context
) : ContextWrapper(base) {

    private val res: Resources by lazy {
        val baseResources = super.getResources()
        return@lazy PhilologyResources(baseResources)
    }

    override fun getResources(): Resources = res

}