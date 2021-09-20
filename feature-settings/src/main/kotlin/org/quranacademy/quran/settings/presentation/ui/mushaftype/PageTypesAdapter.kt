package org.quranacademy.quran.settings.presentation.ui.mushaftype

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.PagerAdapter
import kotlinx.android.synthetic.main.item_mushaf_page_info.view.*
import org.quranacademy.quran.presentation.extensions.inflate
import org.quranacademy.quran.settings.R
import org.quranacademy.quran.settings.presentation.mvp.mushaftype.PageTypeInfo

class PageTypesAdapter(
        private val pageTypes: List<PageTypeInfo>,
        private val onPageTypeSelected: (PageTypeInfo) -> Unit
) : PagerAdapter() {

    private val itemViews = mutableMapOf<Int, View>()

    override fun getCount(): Int = 2

    override fun instantiateItem(parent: ViewGroup, position: Int): Any {
        val typeInfo = pageTypes[position]

        val itemView = parent.inflate(R.layout.item_mushaf_page_info)
        itemViews[position] = itemView
        itemView.title.text = typeInfo.title
        itemView.description.text = typeInfo.description
        itemView.selectTypeButton.setOnClickListener {
            onPageTypeSelected(typeInfo)
        }

        if (!typeInfo.isImageDownloaded()) {

        }

        typeInfo.onImageDownloadListener {
            val options = BitmapFactory.Options()
            options.inPreferredConfig = Bitmap.Config.ALPHA_8
            val pageImageBitmap = BitmapFactory.decodeFile(typeInfo.previewImageFile.absolutePath, options)
            itemView.previewImage.setImageDrawable(BitmapDrawable(parent.resources, pageImageBitmap))
        }


        parent.addView(itemView)

        return itemView
    }

    override fun destroyItem(parent: ViewGroup, position: Int, view: Any) {
        itemViews.remove(position)
        parent.removeView(view as View)
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view === `object`
    }

}