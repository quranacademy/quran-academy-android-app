package org.quranacademy.quran.data.network.typeadapters

import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter
import org.joda.time.DateTime

object DateTimeTypeAdapter : TypeAdapter<DateTime>() {

    // to JSON
    override fun write(writer: JsonWriter, dateTime: DateTime?) {
        when {
            dateTime != null -> writer.value(dateTime.toString())
            else -> writer.nullValue()
        }
    }

    //from JSON
    override fun read(reader: JsonReader): DateTime {
        return DateTime(reader.nextString())
    }

}