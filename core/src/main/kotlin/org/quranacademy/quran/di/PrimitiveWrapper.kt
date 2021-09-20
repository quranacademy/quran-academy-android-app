package org.quranacademy.quran.di

/**
 * @author Konstantin Tskhovrebov (aka terrakok) on 09.07.17.
 */
data class PrimitiveWrapper<out T>(val value: T) // see: https://youtrack.jetbrains.com/issue/KT-18918
