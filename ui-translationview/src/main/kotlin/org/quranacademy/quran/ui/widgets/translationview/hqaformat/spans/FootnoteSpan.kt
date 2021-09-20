package org.quranacademy.quran.ui.widgets.translationview.hqaformat.spans

import android.text.style.ClickableSpan
import android.view.View

class FootnoteSpan(
        private val onClickListener: () -> Unit
) : ClickableSpan() {

    override fun onClick(widget: View) {
        onClickListener()
    }

}