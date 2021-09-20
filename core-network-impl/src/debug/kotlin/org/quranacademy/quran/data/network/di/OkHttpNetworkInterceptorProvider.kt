package org.quranacademy.quran.data.network.di

import com.facebook.stetho.okhttp3.StethoInterceptor
import okhttp3.Interceptor
import javax.inject.Inject
import javax.inject.Provider

class OkHttpNetworkInterceptorProvider @Inject constructor() : Provider<List<Interceptor>> {

    override fun get(): List<Interceptor> {
        val stethoInterceptor = StethoInterceptor()
        return listOf(stethoInterceptor)
    }

}