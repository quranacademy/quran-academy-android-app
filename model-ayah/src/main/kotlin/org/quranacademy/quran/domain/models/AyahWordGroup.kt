package org.quranacademy.quran.domain.models

data class AyahWordGroup(val translationText: String) : AyahWordItem {

    val words: MutableList<AyahWord> = mutableListOf()

}