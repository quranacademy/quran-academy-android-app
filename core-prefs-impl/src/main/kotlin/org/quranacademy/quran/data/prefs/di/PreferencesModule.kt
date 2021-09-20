package org.quranacademy.quran.data.prefs.di

import org.quranacademy.quran.data.prefs.*
import org.quranacademy.quran.di.bind
import org.quranacademy.quran.di.toType
import toothpick.config.Module

class PreferencesModule : Module() {

    init {
        bind(GeneralPreferences::class).singletonInScope()
        bind(AppPreferences::class)
                .toType(AppPreferencesImpl::class)
                .singletonInScope()
        bind(AppearancePreferences::class)
                .toType(AppearancePreferencesImpl::class)
                .singletonInScope()
    }

}