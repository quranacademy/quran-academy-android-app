package org.quranacademy.quran.data.network

import okhttp3.ConnectionSpec
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.TlsVersion
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class OkHttpProviderImpl @Inject constructor() : OkHttpProvider {

    override fun create(
            interceptors: List<Interceptor>,
            networkInterceptors: List<Interceptor>
    ): OkHttpClient.Builder {
        val spec = ConnectionSpec.Builder(ConnectionSpec.MODERN_TLS)
                .tlsVersions(TlsVersion.TLS_1_0)
                .allEnabledCipherSuites()
                .build()
        return OkHttpClient.Builder().apply {
            connectionSpecs(listOf(spec, ConnectionSpec.CLEARTEXT))
            connectTimeout(15, TimeUnit.SECONDS)
            readTimeout(15, TimeUnit.SECONDS)
            writeTimeout(15, TimeUnit.SECONDS)
            for (interceptor in interceptors) {
                addInterceptor(interceptor)
            }

            for (networkInterceptor in networkInterceptors) {
                addNetworkInterceptor(networkInterceptor)
            }
        }
    }

}