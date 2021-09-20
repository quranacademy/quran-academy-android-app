package org.quranacademy.quran.mushaf.presentation.ui.page.pagelayout

import android.content.Context
import android.util.AttributeSet
import android.widget.ScrollView

class ObservableScrollView @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyle: Int = 0
) : ScrollView(context, attrs, defStyle) {

    var scrollListener: ((scrollView: ObservableScrollView, x: Int, y: Int, oldx: Int, oldy: Int) -> Unit)? = null

    override fun onScrollChanged(l: Int, t: Int, oldl: Int, oldt: Int) {
        super.onScrollChanged(l, t, oldl, oldt)
        scrollListener?.invoke(this, l, t, oldl, oldt)
    }

}