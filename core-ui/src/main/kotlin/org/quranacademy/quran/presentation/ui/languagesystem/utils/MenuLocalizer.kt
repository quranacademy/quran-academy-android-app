package org.quranacademy.quran.presentation.ui.languagesystem.utils

import android.content.Context
import android.view.Menu
import org.quranacademy.quran.presentation.ui.languagesystem.Philology

object MenuLocalizer {

    fun localize(context: Context, menu: Menu) {
        (0 until menu.size()).forEach { index ->
            val menuItem = menu.getItem(index)
            menuItem.title =
                    getLocalizedString(context, menuItem.title.toString())
        }
    }

    private fun getLocalizedString(context: Context, text: String): String {
        val stringId = Philology.getStringId(text)
        return if (stringId != null) {
            context.getString(stringId)
        } else {
            text
        }
    }

}

fun Menu.localizeMenu(context: Context) = MenuLocalizer.localize(context, this)