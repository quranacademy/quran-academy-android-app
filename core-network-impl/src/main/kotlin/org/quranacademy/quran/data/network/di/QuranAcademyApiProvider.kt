package org.quranacademy.quran.data.network.di

import okhttp3.Interceptor
import org.quranacademy.quran.data.network.ApiBuilder
import org.quranacademy.quran.data.network.ApiConstants
import org.quranacademy.quran.data.network.QuranAcademyApi
import org.quranacademy.quran.data.network.interceptors.AppDataInterceptor
import org.quranacademy.quran.data.network.interceptors.NetworkCheckInterceptor
import javax.inject.Inject
import javax.inject.Provider

class QuranAcademyApiProvider @Inject constructor(
        private val apiBuilder: ApiBuilder,
        @OkHttpInterceptors private val interceptors: List<Interceptor>,
        @OkHttpNetworkInterceptors private val neworkInterceptors: List<Interceptor>,
        private val networkCheckInterceptor: NetworkCheckInterceptor,
        private val appDataInterceptor: AppDataInterceptor
) : Provider<QuranAcademyApi> {

    override fun get(): QuranAcademyApi {
        val resultInterceptorsList = interceptors.plus(arrayOf(
                networkCheckInterceptor,
                appDataInterceptor
        ))

        return apiBuilder.createApi(
                apiClass = QuranAcademyApi::class,
                host = ApiConstants.QURAN_ACADEMY_API_ENDPOINT,
                interceptors = resultInterceptorsList,
                networkInterceptors = neworkInterceptors
        )
    }

}