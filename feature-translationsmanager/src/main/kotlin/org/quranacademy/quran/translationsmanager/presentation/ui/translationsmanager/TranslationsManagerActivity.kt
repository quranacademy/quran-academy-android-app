package org.quranacademy.quran.translationsmanager.presentation.ui.translationsmanager

import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import org.quranacademy.quran.presentation.mvp.routing.screens.TranslationsManagerScreen
import org.quranacademy.quran.presentation.mvp.routing.screens.TranslationsOrderScreen
import org.quranacademy.quran.presentation.ui.base.BaseThemedActivity
import org.quranacademy.quran.translationsmanager.R

class TranslationsManagerActivity : BaseThemedActivity() {

    override val layoutRes = R.layout.activity_translations

    private val toolbar: Toolbar by lazy { findViewById<Toolbar>(R.id.toolbar) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val screen = screenResolver.getScreen<TranslationsManagerScreen>(this)
        val isTafseers = screen.isTafseers

        val translationsFragment = TranslationsManagerFragment.newInstance(isTafseers, false)
        supportFragmentManager
                .beginTransaction()
                .replace(R.id.translationsListContainer, translationsFragment, "TranslationsManagerFragment")
                .commit()

        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp)
        toolbar.setNavigationOnClickListener { translationsFragment.onBackPressed() }
        toolbar.setTitle(if (isTafseers) R.string.tafseers else R.string.translations)
        toolbar.inflateMenu(R.menu.translations_manager_menu)
        toolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.translations_order -> navigator.goForward(TranslationsOrderScreen())
            }
            return@setOnMenuItemClickListener true
        }
        toolbar.menu.localizeMenu()
    }

}