package org.quranacademy.quran.data.database.models

import com.raizlabs.android.dbflow.annotation.Column
import com.raizlabs.android.dbflow.annotation.PrimaryKey
import com.raizlabs.android.dbflow.annotation.Table
import org.quranacademy.quran.data.database.AppDatabase

@Table(name = "Surahs", database = AppDatabase::class, useBooleanGetterSetters = false)
class SurahModel() {

    @PrimaryKey(autoincrement = true)
    var id: Long = 0

    @Column(name = "surah_number")
    var surahNumber: Int = 0

    @Column(name = "bismillah_pre")
    var bismillahPre: Boolean = true

    //@Column(name = "revelation_place")
    //lateinit var revelationPlace: String
    //@Column(name = "revelation_order")
    //var revelationOrder: Int = 0
    @Column(name = "ayahs_count")
    var ayahsCount: Int = 0

    @Column(name = "juz_num")
    var juzNumber: Int = 0

    @Column(name = "hizb_num")
    var hizbNumber: Int = 0

    @Column(name = "rub_num")
    var rubNumber: Int = 0

    @Column(name = "arabic_name")
    lateinit var arabicName: String

    constructor(
            id: Long,
            surahNumber: Int,
            bismillahPre: Boolean,
            //revelationPlace: String,
            //revelationOrder: Int,
            juzNumber: Int,
            hizbNumber: Int,
            rubNumber: Int,
            ayahsCount: Int,
            arabicName: String
    ) : this() {
        this.id = id
        this.surahNumber = surahNumber
        this.bismillahPre = bismillahPre
        //this.revelationPlace = revelationPlace
        this.juzNumber = juzNumber
        this.hizbNumber = hizbNumber
        this.rubNumber = rubNumber
        this.ayahsCount = ayahsCount
        this.arabicName = arabicName
    }

}