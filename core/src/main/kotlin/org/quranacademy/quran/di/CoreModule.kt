package org.quranacademy.quran.di

import org.quranacademy.quran.data.AssetsFileExtractor
import org.quranacademy.quran.data.PathProvider
import org.quranacademy.quran.data.mushaf.ImageFilePathProvider
import org.quranacademy.quran.domain.commons.Clock
import org.quranacademy.quran.domain.commons.EmailValidator
import toothpick.config.Module

class CoreModule : Module() {

    init {
        bind(PathProvider::class).singletonInScope()
        bind(ImageFilePathProvider::class).singletonInScope()
        bind(EmailValidator::class).singletonInScope()
        bind(Clock::class).singletonInScope()
        bind(AssetsFileExtractor::class).singletonInScope()
    }

}