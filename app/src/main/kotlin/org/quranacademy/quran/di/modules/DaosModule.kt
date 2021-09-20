@file:JvmName("DataModuleKt")

package org.quranacademy.quran.di.modules

import org.quranacademy.quran.data.database.daos.*
import org.quranacademy.quran.di.bind
import toothpick.config.Module

class DaosModule : Module() {

    init {
        bind(AyahsBookmarksDao::class).singletonInScope()
        bind(AyahsDao::class).singletonInScope()
        bind(AyahTranslationsDao::class).singletonInScope()
        bind(LanguagesDao::class).singletonInScope()
        bind(PageBookmarksDao::class).singletonInScope()
        bind(PageBoundsDao::class).singletonInScope()
        bind(SurahsDao::class).singletonInScope()
        bind(SurahsNameTranslationsDao::class).singletonInScope()
        bind(TranslationsDao::class).singletonInScope()
        bind(WordByWordTranslationsDao::class).singletonInScope()
        bind(RecitationsDao::class).singletonInScope()
    }

}