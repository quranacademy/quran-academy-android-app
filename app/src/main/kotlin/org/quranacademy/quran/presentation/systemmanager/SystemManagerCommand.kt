package org.quranacademy.quran.presentation.systemmanager

import android.app.Activity

interface SystemManagerCommand {

    fun execute(activity: Activity)

}