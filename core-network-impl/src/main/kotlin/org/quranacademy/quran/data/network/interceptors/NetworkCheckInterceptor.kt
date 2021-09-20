package org.quranacademy.quran.data.network.interceptors

import okhttp3.Interceptor
import okhttp3.Response
import org.quranacademy.quran.data.network.NetworkChecker
import org.quranacademy.quran.domain.exceptions.NoNetworkException
import java.io.IOException
import javax.inject.Inject

class NetworkCheckInterceptor @Inject constructor(
        private val networkChecker: NetworkChecker
) : Interceptor {

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val requestBuilder = chain.request().newBuilder()
        if (!networkChecker.isConnected) {
            throw NoNetworkException("No network connection. URL: ${chain.request().url()}")
        }

        return chain.proceed(requestBuilder.build())
    }

}
