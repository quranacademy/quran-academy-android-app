package org.quranacademy.quran.presentation.ui.global

import androidx.recyclerview.widget.DiffUtil

abstract class BaseDiffUtils<T>(
        private val oldList: List<T>,
        private val newList: List<T>
) : DiffUtil.Callback() {

    override fun getOldListSize(): Int {
        return oldList.size
    }

    override fun getNewListSize(): Int {
        return newList.size
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldItem = oldList[oldItemPosition]
        val newItem = newList[newItemPosition]
        return areItemsTheSame(oldItem, newItem)
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldItem = oldList[oldItemPosition]
        val newItem = newList[newItemPosition]
        return areContentsTheSame(oldItem, newItem)
    }

    abstract fun areItemsTheSame(old: T, new: T): Boolean

    abstract fun areContentsTheSame(old: T, new: T): Boolean

    protected inline fun <reified T> compare(
            one: Any,
            two: Any,
            check: (one: T, two: T) -> Boolean
    ) {
        if (one::class == two::class && one is T) check(one as T, two as T)
    }

}