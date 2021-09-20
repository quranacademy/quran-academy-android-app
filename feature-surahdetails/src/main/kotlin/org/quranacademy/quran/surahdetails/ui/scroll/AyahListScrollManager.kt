package org.quranacademy.quran.surahdetails.ui.scroll

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class AyahListScrollManager(
        private val ayahsList: RecyclerView
) {

    private val smoothScroller = AyahsListSmoothScroller(ayahsList.context)
    private val layoutManger = ayahsList.layoutManager as LinearLayoutManager
    private val scrollListener = AyahsListScrollListener()

    init {
        //отключаем анимации, которые показываются при вызове notifyItemChanged
        //т. к. при частом обновлении (во время подсветки слов плеера), список начинает мигать,
        //что очень раздражает
        ayahsList.itemAnimator = null
        ayahsList.addOnScrollListener(scrollListener)
    }

    fun scrollToAyah(position: Int) {
        layoutManger.scrollToPositionWithOffset(position, 0)
    }

    fun smoothScrollToAyah(position: Int) {
        smoothScroller.targetPosition = position
        layoutManger.startSmoothScroll(smoothScroller)
    }

    fun getFirstVisibleAyah() = layoutManger.findFirstVisibleItemPosition()

    fun onListScrollListener(listener: (dx: Int, dy: Int) -> Unit) {
        ayahsList.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                listener(dx, dy)
            }
        })
    }

    fun setOnScrollEndListener(onScrollEndListener: (isScrollEnd: Boolean) -> Unit) {
        ayahsList.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                if (!recyclerView.canScrollVertically(1) && newState == RecyclerView.SCROLL_STATE_IDLE) {
                    onScrollEndListener(true)
                } else if (recyclerView.canScrollVertically(1) && newState == RecyclerView.SCROLL_STATE_IDLE) {
                    onScrollEndListener(false)
                }
            }
        })
    }

}