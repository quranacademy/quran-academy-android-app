package org.quranacademy.quran.data.network.interceptors

import okhttp3.Interceptor
import okhttp3.Response

class ResponseInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val response = chain.proceed(chain.request())
        return response.newBuilder()
                .addHeader("Content-Type", "application/json; charset=WINDOWS-1251")
                .build()
    }

}