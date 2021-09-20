package org.quranacademy.quran.presentation.mvp

import org.quranacademy.quran.core.ui.R
import org.quranacademy.quran.domain.commons.ResourcesManager
import org.quranacademy.quran.domain.exceptions.NoNetworkException
import org.quranacademy.quran.domain.exceptions.ServerError
import retrofit2.HttpException
import timber.log.Timber
import java.io.IOException
import java.net.UnknownHostException
import javax.inject.Inject
import javax.net.ssl.SSLHandshakeException

class ErrorHandler @Inject constructor(
        private val resourcesManager: ResourcesManager
) {

    fun proceed(error: Throwable, messageListener: (String) -> Unit = {}) {
        Timber.e(error)
        messageListener(getMessageForError(error))
    }

    fun getMessageForError(error: Throwable): String {
        val messageResId = when (error) {
            is ServerError -> getNetworkErrorMessage(error)
            is NoNetworkException -> R.string.network_error
            is UnknownHostException -> R.string.check_network_dialog_title
            is SSLHandshakeException -> R.string.ssl_error_check_device_time_message
            is IOException -> R.string.network_error
            else -> R.string.unknown_error
        }
        return resourcesManager.getString(messageResId)
    }

    private fun getNetworkErrorMessage(error: Throwable): Int = when (error) {
        is HttpException -> when (error.code()) {
            304 -> R.string.not_modified_error
            400 -> R.string.bad_request_error
            401 -> R.string.unauthorized_error
            403 -> R.string.forbidden_error
            404 -> R.string.not_found_error
            405 -> R.string.method_not_allowed_error
            409 -> R.string.conflict_error
            422 -> R.string.unprocessable_error
            500 -> R.string.server_error_error
            else -> R.string.unknown_error
        }
        else -> R.string.unknown_error
    }

}
