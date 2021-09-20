package org.quranacademy.quran.domain.commons

interface ResourcesManager {

    fun getString(resourceId: Int, vararg formatArgs: Any): String

    fun getInteger(resourceId: Int): Int

    fun getResIdByName(resIdName: String, resType: String): Int

}
