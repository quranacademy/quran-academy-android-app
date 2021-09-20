package org.quranacademy.quran.presentation

import android.content.Context

import org.quranacademy.quran.domain.commons.ResourcesManager
import javax.inject.Inject

class ResourcesManagerImpl @Inject constructor(context: Context) : ResourcesManager {

    private val resources = context.resources
    private val packageName = context.packageName

    override fun getString(resourceId: Int, vararg formatArgs: Any): String {
        return resources.getString(resourceId, *formatArgs)
    }

    override fun getInteger(resourceId: Int): Int {
        return resources.getInteger(resourceId)
    }

    override fun getResIdByName(resIdName: String, resType: String): Int {
        return resources.getIdentifier(resIdName, resType, packageName)
    }

}
