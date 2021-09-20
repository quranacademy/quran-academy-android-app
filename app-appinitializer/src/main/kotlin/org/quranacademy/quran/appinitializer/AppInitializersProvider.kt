package org.quranacademy.quran.appinitializer

interface AppInitializersProvider {

    fun getInitializers(): List<AppInitializerElement>

}