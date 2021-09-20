package org.quranacademy.quran.languagesmanager.presentation.ui

import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hannesdorfmann.adapterdelegates3.AdapterDelegate
import kotlinx.android.synthetic.main.item_language.view.*
import org.quranacademy.quran.languagesmanager.R
import org.quranacademy.quran.languagesmanager.presentation.mvp.LanguageUIModel
import org.quranacademy.quran.presentation.extensions.inflate
import org.quranacademy.quran.presentation.extensions.visible
import org.quranacademy.quran.presentation.ui.global.getCurrentAppLanguage

class LanguageAdapterDelegate(
        private val downloadClickListener: (LanguageUIModel) -> Unit,
        private val removeClickListener: (LanguageUIModel) -> Unit,
        private val enableClickListener: (LanguageUIModel) -> Unit
) : AdapterDelegate<MutableList<Any>>() {

    private val currentLanguage = getCurrentAppLanguage()

    override fun isForViewType(items: MutableList<Any>, position: Int) =
            items[position] is LanguageUIModel

    override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder =
            ViewHolder(parent.inflate(R.layout.item_language))

    override fun onBindViewHolder(items: MutableList<Any>,
                                  position: Int,
                                  viewHolder: RecyclerView.ViewHolder,
                                  payloads: MutableList<Any>) =
            (viewHolder as ViewHolder).bind(items[position] as LanguageUIModel)

    private inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        private lateinit var language: LanguageUIModel

        fun bind(language: LanguageUIModel) {
            this.language = language

            itemView.enableLanguage.isChecked = language.isEnabled
            itemView.enableLanguage.isEnabled = language.isDownloaded

            itemView.languageName.text = "${language.name} (${language.englishName})"
            itemView.languageName.isEnabled = language.isDownloaded
            itemView.languageName.gravity = if (currentLanguage.isRtl) Gravity.RIGHT else Gravity.LEFT

            if (language.isDownloaded) {
                itemView.rightImage.setImageResource(R.drawable.ic_remove_black_24dp)
                itemView.rightImage.visible(!language.isEnabled) //if language is enabled, we can't remove it
                itemView.rightImage.setOnClickListener { removeClickListener(language) }
                itemView.setOnClickListener { enableClickListener(language) }
            } else {
                itemView.rightImage.visible(true)
                itemView.rightImage.setImageResource(R.drawable.ic_download_black_24dp)
                itemView.rightImage.setOnClickListener { downloadClickListener(language) }
                itemView.setOnClickListener { downloadClickListener(language) }
            }
        }
    }

}