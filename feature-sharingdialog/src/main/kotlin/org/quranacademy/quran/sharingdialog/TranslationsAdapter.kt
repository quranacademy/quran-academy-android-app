package org.quranacademy.quran.sharingdialog

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_translation_sharing.view.*
import org.quranacademy.quran.domain.models.Translation
import org.quranacademy.quran.presentation.extensions.inflate

class TranslationsAdapter(
        private val onTranslationSelected: (TranslationSharingModel) -> Unit
) : RecyclerView.Adapter<TranslationsAdapter.Holder>() {

    private var translations = listOf<TranslationSharingModel>()

    fun updateData(translations: List<TranslationSharingModel>) {
        this.translations = translations
        notifyDataSetChanged()
    }

    override fun getItemCount() = translations.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view = parent.inflate(R.layout.item_translation_sharing)
        return Holder(view)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val translation = translations[position]
        val itemView = holder.itemView

        itemView.translationName.text = translation.name
        itemView.translationSelectionCheckbox.isChecked = translation.isSelected
        itemView.setOnClickListener {
            onTranslationSelected(translation)
        }
    }

    class Holder(itemView: View) : RecyclerView.ViewHolder(itemView)

}