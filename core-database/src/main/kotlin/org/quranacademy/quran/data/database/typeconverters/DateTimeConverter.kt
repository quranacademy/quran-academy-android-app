package org.quranacademy.quran.data.database.typeconverters

import com.raizlabs.android.dbflow.converter.TypeConverter
import org.joda.time.DateTime

@com.raizlabs.android.dbflow.annotation.TypeConverter
class DateTimeConverter : TypeConverter<Long, DateTime>() {

    override fun getDBValue(model: DateTime?): Long? {
        return model?.millis
    }

    override fun getModelValue(data: Long?): DateTime? {
        return if (data == null) null else DateTime(data)
    }
}