package org.quranacademy.quran.data.network.responses

import com.google.gson.annotations.SerializedName

class SurahResponse {

    @SerializedName("id")
    var id: Long = 0

    @SerializedName("sura_number")
    var surahNumber: Int = 0

    @SerializedName("show_basmala")
    var bismillahPre: Boolean = true

    //@SerializedName("revelation_order")
    //var revelationOrder: Int = 0
    //@SerializedName("revelation_place")
    //lateinit var revelationPlace: String
    @SerializedName("juz_num")
    var juzNumber: Int = 0

    @SerializedName("hizb_num")
    var hizbNumber: Int = 0

    @SerializedName("rub_num")
    var rubNumber: Int = 0

    @SerializedName("ayat_count")
    var ayahsCount: Int = 0

    @SerializedName("name")
    lateinit var name: TranslatedName

    class TranslatedName {
        @SerializedName("arabic")
        lateinit var arabicName: String

        @SerializedName("translation")
        lateinit var translatedName: String

        @SerializedName("transliteration")
        lateinit var transliteratedName: String
    }

}