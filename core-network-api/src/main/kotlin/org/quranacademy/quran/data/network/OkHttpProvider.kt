package org.quranacademy.quran.data.network

import okhttp3.Interceptor
import okhttp3.OkHttpClient

interface OkHttpProvider {

    fun create(
            interceptors: List<Interceptor> = emptyList(),
            networkInterceptors: List<Interceptor> = emptyList()
    ): OkHttpClient.Builder

}