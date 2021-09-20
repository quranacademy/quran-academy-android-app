package org.quranacademy.quran.domain.commons

import android.app.Activity

interface SystemManager {

    fun copyText(text: String)

    fun shareText(text: String)

    fun attachActivity(activity: Activity)

    fun detachActivity()

}
