package org.quranacademy.quran.presentation.extensions

fun <T> MutableList<T>.replace(oldItem: T, newItem: T) {
    val itemIndex = indexOf(oldItem)
    if (itemIndex != -1) {
        set(itemIndex, newItem)
    } else {
        throw IllegalArgumentException("Element oldItem not found")
    }
}

fun <T> MutableList<T>.replace(oldItem: T, newItem: T, comparator: (o1: T, o2: T) -> Boolean) {
    val itemIndex = indexOfFirst { comparator(oldItem, it) }
    if (itemIndex != -1) {
        set(itemIndex, newItem)
    } else {
        throw IllegalArgumentException("Element oldItem not found")
    }
}

fun <T> MutableList<T>.moveItem(oldPos: Int, newPos: Int) {
    val tmp = this[oldPos]
    if (oldPos < newPos) for (i in oldPos until newPos) this[i] = this[i + 1]
    else for (i in oldPos - 1 downTo newPos) this[i + 1] = this[i]
    this[newPos] = tmp
}