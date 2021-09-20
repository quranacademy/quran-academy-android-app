package org.quranacademy.quran.wordbywordmanager.presentation.ui

import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import org.quranacademy.quran.presentation.ui.base.BaseThemedActivity
import org.quranacademy.quran.wordbywordmanager.R

class WordByWordTranslationsManagerActivity : BaseThemedActivity() {

    override val layoutRes = R.layout.activity_word_by_word_translations

    private val toolbar: Toolbar by lazy { findViewById<Toolbar>(R.id.toolbar) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val translationsFragment = WordByWordTranslationsManagerFragment.newInstance(false)
        supportFragmentManager
                .beginTransaction()
                .replace(R.id.translationsListContainer, translationsFragment, "TranslationsManagerFragment")
                .commit()

        toolbar.setTitle(R.string.word_by_word)
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp)
        toolbar.setNavigationOnClickListener {
            translationsFragment.onBackPressed()
        }
    }

}