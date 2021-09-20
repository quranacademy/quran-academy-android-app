package org.quranacademy.quran.presentation.ui.languagesystem.repository

interface PhilologyRepository {

    fun getString(key: String): String?

    fun getQuantityString(key: String, quantity: Int): String?

}