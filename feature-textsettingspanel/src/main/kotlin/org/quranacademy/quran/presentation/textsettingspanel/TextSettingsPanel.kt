package org.quranacademy.quran.presentation.textsettingspanel

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.android.synthetic.main.panel_layout.*
import org.quranacademy.quran.presentation.extensions.inflateThemed
import org.quranacademy.quran.presentation.mvp.routing.screens.TajweedRulesScreen
import org.quranacademy.quran.presentation.ui.base.BaseDialogFragment
import org.quranacademy.quran.presentation.ui.textsettingspanel.R

class TextSettingsPanel : BaseDialogFragment() {

    var onDismissListener: (() -> Unit)? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return BottomSheetDialog(requireContext())
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val themeResId = appearanceManager.getAppThemeResId(appearanceManager.getCurrentAppTheme())
        return requireContext().inflateThemed(R.layout.panel_layout, themeResId, container)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        textSettingsToolbar.setNavigationOnClickListener { dismiss() }
        textSettingsToolbar.inflateMenu(R.menu.text_settings_menu)
        textSettingsToolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.openQuranReadingRulesDialog -> navigator.goForward(TajweedRulesScreen())
            }
            return@setOnMenuItemClickListener true
        }

        val translationsFragment = TextSettingsFragment.newInstance()
        childFragmentManager
                .beginTransaction()
                .replace(R.id.panelContentContainer, translationsFragment, "TextSettingsFragment")
                .commit()
    }

    override fun onDestroy() {
        super.onDestroy()

        onDismissListener?.invoke()
    }

}