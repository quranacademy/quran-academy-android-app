package org.quranacademy.quran.presentation.textsettingspanel

import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import org.quranacademy.quran.presentation.ui.base.BaseThemedActivity
import org.quranacademy.quran.presentation.ui.textsettingspanel.R

class QuranTextSettingsActivity : BaseThemedActivity() {

    override val layoutRes = R.layout.activity_quran_text_settings

    private val toolbar: Toolbar by lazy { findViewById<Toolbar>(R.id.toolbar) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val translationsFragment = TextSettingsFragment.newInstance()
        supportFragmentManager
                .beginTransaction()
                .replace(R.id.textSettingsContainer, translationsFragment, "TextSettingsFragment")
                .commit()

        toolbar.setTitle(R.string.prefs_quran_text_settings_title)
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp)
        toolbar.setNavigationOnClickListener { finish() }
    }

}