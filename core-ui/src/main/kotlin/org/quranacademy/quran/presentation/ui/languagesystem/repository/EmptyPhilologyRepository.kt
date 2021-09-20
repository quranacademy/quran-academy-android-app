package org.quranacademy.quran.presentation.ui.languagesystem.repository

object EmptyPhilologyRepository : PhilologyRepository {

    override fun getString(key: String): String? = null

    override fun getQuantityString(key: String, quantity: Int): String? = null

}