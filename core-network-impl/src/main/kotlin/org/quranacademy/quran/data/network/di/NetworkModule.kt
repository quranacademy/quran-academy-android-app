package org.quranacademy.quran.data.network.di

import org.quranacademy.quran.data.network.*
import org.quranacademy.quran.di.bind
import toothpick.config.Module

class NetworkModule : Module() {

    init {
        bind(GsonProvider::class).singletonInScope()
        bind(OkHttpProvider::class).toInstance(OkHttpProviderImpl())
        bind(ApiBuilder::class).singletonInScope()

        bind(List::class)
                .withName(OkHttpInterceptors::class.java)
                .toProvider(OkHttpInterceptorProvider::class.java)
        bind(List::class)
                .withName(OkHttpNetworkInterceptors::class.java)
                .toProvider(OkHttpNetworkInterceptorProvider::class.java)

        bind(QuranAcademyApi::class)
                .toProvider(QuranAcademyApiProvider::class.java)
                .singletonInScope()
    }

}