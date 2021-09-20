package org.quranacademy.quran.data.network.di

import okhttp3.Interceptor
import okhttp3.logging.HttpLoggingInterceptor
import org.quranacademy.quran.data.network.interceptors.AppDataInterceptor
import javax.inject.Inject
import javax.inject.Provider

class OkHttpInterceptorProvider @Inject constructor(
        private val appDataInterceptor: AppDataInterceptor
) : Provider<List<Interceptor>> {

    override fun get(): List<Interceptor> {
        val httpLoggingInterceptor = HttpLoggingInterceptor()
        httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
        return listOf(httpLoggingInterceptor, appDataInterceptor)
    }

}