package org.quranacademy.quran.data.network

import com.google.gson.FieldNamingPolicy
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import org.joda.time.DateTime
import org.quranacademy.quran.data.network.typeadapters.DateTimeTypeAdapter
import javax.inject.Inject

class GsonProvider @Inject constructor() {

    fun create(): Gson {
        return GsonBuilder()
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .registerTypeAdapter(DateTime::class.java, DateTimeTypeAdapter)
                .create()
    }

}