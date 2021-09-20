package org.quranacademy.quran.presentation.ui.global

import androidx.recyclerview.widget.RecyclerView

class OnScrollListener(
        private val state: State,
        private val onScrollingEndedListener: () -> Unit
) : RecyclerView.OnScrollListener() {

    enum class State(internal val code: Int) {
        STARTED(RecyclerView.SCROLL_STATE_DRAGGING),
        ENDED(RecyclerView.SCROLL_STATE_IDLE)
    }

    private var previousState = RecyclerView.SCROLL_STATE_IDLE

    override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
        val stateChanged = previousState != newState
        if (stateChanged) {
            if (newState == state.code) {
                onScrollingEndedListener()
            }
            previousState = newState
        }

    }

}