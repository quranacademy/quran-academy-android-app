package org.quranacademy.quran.settings.presentation.ui

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_storage.view.*
import org.quranacademy.quran.presentation.extensions.bytes2String
import org.quranacademy.quran.presentation.extensions.inflate
import org.quranacademy.quran.presentation.ui.global.getCurrentAppLanguage
import org.quranacademy.quran.settings.R
import org.quranacademy.quran.settings.domain.StoragesInfo

class StoragesAdapter(
        private val storagesInfo: StoragesInfo,
        private val onStorageSelected: (StoragesInfo.Storage) -> Unit
) : RecyclerView.Adapter<StoragesAdapter.Holder>() {

    private val storages = listOf(
            storagesInfo.internalStorage,
            storagesInfo.externalStorage!!
    )
    private val titles = listOf(
            storagesInfo.internalStorage to R.string.prefs_internal_storage,
            storagesInfo.externalStorage!! to R.string.prefs_external_storage
    ).toMap()
    private val isLanguageRtl = getCurrentAppLanguage().isRtl

    override fun getItemCount() = 2

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view = parent.inflate(R.layout.item_storage)
        return Holder(view)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val storage = storages[position]
        val itemView = holder.itemView
        val context = itemView.context

        itemView.isSelectedIndicator.isChecked = storagesInfo.currentStoragePath == storage.folderPath
        itemView.storageTitle.setText(titles[storage]!!)
        itemView.storageInfo.text = context.getString(
                R.string.free_space_info,
                storage.freeSpace.bytes2String(isLanguageRtl)
        )

        itemView.setOnClickListener {
            onStorageSelected(storage)
        }
    }

    class Holder(itemView: View) : RecyclerView.ViewHolder(itemView)

}