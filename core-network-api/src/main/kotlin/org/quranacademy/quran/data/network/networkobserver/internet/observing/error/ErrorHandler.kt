package org.quranacademy.quran.data.network.networkobserver.internet.observing.error

interface ErrorHandler {

    fun handleError(exception: Exception, message: String)

}
