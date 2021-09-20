package org.quranacademy.quran.player.presentation.playeroptions.reciterselection

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_recitation.view.*
import org.quranacademy.quran.domain.models.Recitation
import org.quranacademy.quran.player.R
import org.quranacademy.quran.presentation.extensions.inflate

class RecitationAdapter(
        private val onRecitationSelected: (Recitation) -> Unit
) : RecyclerView.Adapter<RecitationAdapter.Holder>() {

    private var recitations = listOf<Recitation>()

    fun updateData(recitations: List<Recitation>) {
        this.recitations = recitations
        notifyDataSetChanged()
    }

    override fun getItemCount() = recitations.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view = parent.inflate(R.layout.item_recitation)
        return Holder(view)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val recitation = recitations[position]
        val itemView = holder.itemView

        itemView.recitationTitle.text = recitation.name
        itemView.setOnClickListener {
            onRecitationSelected(recitation)
        }
    }

    class Holder(itemView: View) : RecyclerView.ViewHolder(itemView)

}