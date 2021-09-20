package org.quranacademy.quran.data.network.networkobserver.internet.observing.error

import android.util.Log
import org.quranacademy.quran.data.network.networkobserver.ReactiveNetwork.LOG_TAG

class DefaultErrorHandler : ErrorHandler {

    override fun handleError(exception: Exception, message: String) {
        Log.e(LOG_TAG, message, exception)
    }

}
