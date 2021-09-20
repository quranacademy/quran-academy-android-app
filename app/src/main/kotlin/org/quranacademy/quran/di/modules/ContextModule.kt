package org.quranacademy.quran.di.modules

import android.content.Context
import org.quranacademy.quran.presentation.ui.languagesystem.Philology
import toothpick.config.Module

class ContextModule(context: Context) : Module() {

    init {
        bind(Context::class.java)
                .toInstance(Philology.wrap(context))
    }

}