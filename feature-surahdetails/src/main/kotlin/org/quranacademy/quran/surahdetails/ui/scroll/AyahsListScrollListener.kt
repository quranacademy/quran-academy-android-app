package org.quranacademy.quran.surahdetails.ui.scroll

import androidx.recyclerview.widget.RecyclerView

class AyahsListScrollListener : RecyclerView.OnScrollListener() {

    var isScrolling = false

    override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
        isScrolling = when (newState) {
            RecyclerView.SCROLL_STATE_IDLE -> false
            RecyclerView.SCROLL_STATE_DRAGGING -> true
            RecyclerView.SCROLL_STATE_SETTLING -> true
            else -> throw IllegalStateException()
        }
    }

}