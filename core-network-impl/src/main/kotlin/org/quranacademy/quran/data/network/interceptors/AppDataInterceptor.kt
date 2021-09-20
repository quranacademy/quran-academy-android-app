package org.quranacademy.quran.data.network.interceptors

import okhttp3.Interceptor
import okhttp3.Response
import org.quranacademy.quran.data.network.ApiConstants
import org.quranacademy.quran.data.prefs.AppPreferences
import javax.inject.Inject

class AppDataInterceptor @Inject constructor(
        private val preferences: AppPreferences
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val requestBuilder = originalRequest.newBuilder()
        val urlBuilder = originalRequest.url().newBuilder()

        val url = urlBuilder.build()

        val localeFromHeader = originalRequest.header("Locale")
        val locale = localeFromHeader ?: preferences.getAppLanguage()

        val request = requestBuilder
                .url(url)
                .addHeader("Locale", locale) //язык
                .addHeader("Accept", " application/prs.quranacademy.v${ApiConstants.API_VERSION}+json") //версия API
                .build()

        return chain.proceed(request)
    }
}