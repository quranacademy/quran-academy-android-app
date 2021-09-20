package org.quranacademy.quran.wordbywordmanager.presentation.ui

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hannesdorfmann.adapterdelegates3.AdapterDelegate
import kotlinx.android.synthetic.main.item_word_by_word_translation.view.*
import org.quranacademy.quran.presentation.extensions.inflate
import org.quranacademy.quran.presentation.extensions.visible
import org.quranacademy.quran.wordbywordmanager.R
import org.quranacademy.quran.wordbywordmanager.domain.WordByWordTranslationUIModel

class WordByWordTranslationAdapterDelegate(
        private val downloadClickListener: (WordByWordTranslationUIModel) -> Unit,
        private val updateClickListener: (WordByWordTranslationUIModel) -> Unit,
        private val deleteClickListener: (WordByWordTranslationUIModel) -> Unit,
        private val enableClickListener: (WordByWordTranslationUIModel) -> Unit
) : AdapterDelegate<MutableList<Any>>() {

    override fun isForViewType(items: MutableList<Any>, position: Int) =
            items[position] is WordByWordTranslationUIModel

    override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder =
            ViewHolder(parent.inflate(R.layout.item_word_by_word_translation))

    override fun onBindViewHolder(
            items: MutableList<Any>,
            position: Int,
            viewHolder: RecyclerView.ViewHolder,
            payloads: MutableList<Any>
    ) = (viewHolder as ViewHolder).bind(items[position] as WordByWordTranslationUIModel)

    private inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        private lateinit var translation: WordByWordTranslationUIModel

        fun bind(translation: WordByWordTranslationUIModel) {
            this.translation = translation

            itemView.enableTranslation.isChecked = translation.isEnabled
            itemView.translationTitle.text = translation.name

            itemView.enableTranslation.isEnabled = translation.isDownloaded
            itemView.translationTitle.isEnabled = translation.isDownloaded

            if (translation.isDownloaded) {
                if (translation.isUpdateAvailable) {
                    itemView.updateButton.setImageResource(R.drawable.ic_download_black_24dp)
                    itemView.updateButton.visible(true)
                    itemView.updateButton.setOnClickListener { updateClickListener(translation) }
                } else {
                    itemView.updateButton.visible(false)
                }
                itemView.rightImage.setImageResource(R.drawable.ic_remove_black_24dp)
                itemView.rightImage.visibility = View.VISIBLE
                itemView.rightImage.setOnClickListener { deleteClickListener(translation) }

                itemView.setOnClickListener { enableClickListener(translation) }
            } else {
                itemView.setOnClickListener { downloadClickListener(translation) }

                itemView.updateButton.visible(false)
                itemView.rightImage.visible(true)
                itemView.rightImage.setImageResource(R.drawable.ic_download_black_24dp)
                itemView.rightImage.setOnClickListener { downloadClickListener(translation) }
            }
        }
    }

}