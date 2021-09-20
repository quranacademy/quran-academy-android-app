package org.quranacademy.quran.data.network

import okhttp3.Interceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Inject
import kotlin.reflect.KClass

class ApiBuilder @Inject constructor(
        private val okHttpProvider: OkHttpProvider,
        private val gsonProvider: GsonProvider
) {

    fun <T : Any> createApi(
            apiClass: KClass<T>,
            host: String,
            interceptors: List<Interceptor> = listOf(),
            networkInterceptors: List<Interceptor> = listOf()
    ): T {

        val client = okHttpProvider.create(interceptors, networkInterceptors).build()

        val retrofit = Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create(gsonProvider.create()))
                .client(client)
                .baseUrl(host)
                .build()

        return retrofit.create(apiClass.java)
    }

}