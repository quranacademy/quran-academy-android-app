package org.quranacademy.quran.presentation.ui.widgets.waterfalltoolbar

import android.widget.ScrollView
import androidx.core.widget.NestedScrollView
import androidx.recyclerview.widget.RecyclerView

fun WaterfallToolbar.bindTo(recyclerView: RecyclerView) {
    recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            updateScroll(dy)
        }
    })
}

fun WaterfallToolbar.bindTo(scrollView: ScrollView) {
    scrollView.viewTreeObserver.addOnScrollChangedListener {
        updateScroll(scrollY)
    }
}

fun WaterfallToolbar.bindTo(scrollView: NestedScrollView) {
    scrollView.viewTreeObserver.addOnScrollChangedListener {
        updateScroll(scrollY)
    }
}