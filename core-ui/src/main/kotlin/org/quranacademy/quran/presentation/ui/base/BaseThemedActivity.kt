package org.quranacademy.quran.presentation.ui.base

import android.os.Bundle
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

abstract class BaseThemedActivity : BaseActivity() {

    val currentThemeId: Int by lazy { appearanceManager.getCurrentAppThemeResId() }

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(currentThemeId)
        super.onCreate(savedInstanceState)

        launch {
            appearanceManager.getAppThemeUpdates()
                    .collect { recreate() }
        }

        launch {
            languageManager.getLanguageUpdates()
                    .collect { recreate() }
        }
    }

}