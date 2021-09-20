package org.quranacademy.quran.sharingdialog.sharingmanager

import org.quranacademy.quran.domain.commons.ResourcesManager
import javax.inject.Inject

class LigatureTextProvider @Inject constructor(
        private val resourcesManager: ResourcesManager
) {

    fun getLigatureTextReplacement(ligatureCode: String): String {
        val resName = "ligature_replacement_$ligatureCode"
        return resourcesManager.getString(resourcesManager.getResIdByName(resName, "string"))
    }

}