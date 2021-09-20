package org.quranacademy.quran.mushaf.presentation.mvp.mushafpage

class PageTouchEvent(
        val x: Int,
        val y: Int,
        val type: TouchType
) {

    enum class TouchType {
        SINGLE, DOUBLE, LONG
    }

}