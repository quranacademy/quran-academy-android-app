package org.quranacademy.quran.presentation.ui.global

import me.aartikov.alligator.DestinationType
import me.aartikov.alligator.Screen
import me.aartikov.alligator.TransitionType
import me.aartikov.alligator.animations.AnimationData
import me.aartikov.alligator.animations.SimpleTransitionAnimation
import me.aartikov.alligator.animations.TransitionAnimation
import me.aartikov.alligator.animations.providers.TransitionAnimationProvider
import org.quranacademy.quran.presentation.mvp.routing.screens.MushafScreen
import org.quranacademy.quran.presentation.mvp.routing.screens.SurahDetailsScreen

class DisabledAnimationProvider : TransitionAnimationProvider {

    override fun getAnimation(
            transitionType: TransitionType,
            destinationType: DestinationType,
            screenClassFrom: Class<out Screen>,
            screenClassTo: Class<out Screen>,
            animationData: AnimationData?
    ): TransitionAnimation {
        return when {
            transitionType == TransitionType.REPLACE &&
                    screenClassTo == SurahDetailsScreen::class.java ||
                    screenClassTo == MushafScreen::class.java -> SimpleTransitionAnimation(0, 0)
            else -> TransitionAnimation.DEFAULT
        }
    }

}