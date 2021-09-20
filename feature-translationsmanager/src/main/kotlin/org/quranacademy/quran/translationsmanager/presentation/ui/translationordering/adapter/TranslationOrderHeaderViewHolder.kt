package org.quranacademy.quran.translationsmanager.presentation.ui.translationordering.adapter

import android.view.View
import com.thesurix.gesturerecycler.GestureViewHolder
import kotlinx.android.synthetic.main.item_translation_order.view.*

class TranslationOrderHeaderViewHolder(private val rootView: View) : GestureViewHolder(rootView) {

    override val draggableView: View?
        get() = rootView.dragView

    override fun onItemSelect() {

    }

    override fun onItemClear() {

    }

    override fun canDrag() = true

    override fun canSwipe() = false

}