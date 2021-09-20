package org.quranacademy.quran.di.modules

import me.aartikov.alligator.AndroidNavigator
import me.aartikov.alligator.NavigationContextBinder
import me.aartikov.alligator.Navigator
import me.aartikov.alligator.ScreenResolver
import org.quranacademy.quran.di.bind
import org.quranacademy.quran.presentation.QuranAcademyNavigationFactory
import toothpick.config.Module

class NavigationModule : Module() {

    init {
        val navigator = AndroidNavigator(QuranAcademyNavigationFactory())

        bind(Navigator::class).toInstance(navigator)
        bind(NavigationContextBinder::class).toInstance(navigator)
        bind(ScreenResolver::class).toInstance(navigator.screenResolver)
    }

}
