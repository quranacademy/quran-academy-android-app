package org.quranacademy.quran.di.modules

import org.quranacademy.quran.appinforepository.AppInfoRepositoryImpl
import org.quranacademy.quran.ayahsrepository.AyahsRepositoryImpl
import org.quranacademy.quran.bookmarks.data.ayahbookmarksrepository.AyahBookmarksRepository
import org.quranacademy.quran.bookmarks.data.folders.BookmarkFoldersRepository
import org.quranacademy.quran.bookmarks.data.pagebookmarksrepository.PageBookmarksRepository
import org.quranacademy.quran.bookmarks.data.readinghistory.ReadingHistoryRepository
import org.quranacademy.quran.data.appinforepository.AppInfoRepository
import org.quranacademy.quran.di.bind
import org.quranacademy.quran.di.bindSingleton
import org.quranacademy.quran.di.toType
import org.quranacademy.quran.domain.repositories.*
import org.quranacademy.quran.languagesrepository.LanguagesRepositoryImpl
import org.quranacademy.quran.mushafpageboundsrepository.MushafPageBoundsRepositoryImpl
import org.quranacademy.quran.qurandatarepository.QuranDataRepositoryImpl
import org.quranacademy.quran.quranimagesrepository.QuranImagesRepositoryImpl
import org.quranacademy.quran.quranpagerepository.QuranPageRepositoryImpl
import org.quranacademy.quran.recitationsrepository.RecitationsRepository
import org.quranacademy.quran.surahsrepository.SurahsRepositoryImpl
import org.quranacademy.quran.translationsorder.TranslationsOrderRepositoryImpl
import org.quranacademy.quran.translationsrepository.TranslationsRepositoryImpl
import org.quranacademy.quran.wordbywordrepository.WordByWordTranslationsRepositoryImpl
import toothpick.config.Module

class RepositoriesModule : Module() {

    init {
        bindSingleton<AyahBookmarksRepository>()
        bindSingleton<BookmarkFoldersRepository>()
        bindSingleton<RecitationsRepository>()
        bind(MushafPageBoundsRepository::class)
                .toType(MushafPageBoundsRepositoryImpl::class)
                .singletonInScope()
        bind(AppInfoRepository::class)
                .toType(AppInfoRepositoryImpl::class)
                .singletonInScope()
        bind(AyahsRepository::class)
                .toType(AyahsRepositoryImpl::class)
                .singletonInScope()
        bind(PageBookmarksRepository::class)
                .toType(PageBookmarksRepository::class)
                .singletonInScope()
        bind(ReadingHistoryRepository::class)
                .singletonInScope()
        bind(LanguagesRepository::class)
                .toType(LanguagesRepositoryImpl::class)
                .singletonInScope()
        bind(TranslationsRepository::class)
                .toType(TranslationsRepositoryImpl::class)
                .singletonInScope()
        bind(TranslationsOrderRepository::class)
                .toType(TranslationsOrderRepositoryImpl::class)
                .singletonInScope()
        bind(QuranDataRepository::class)
                .toType(QuranDataRepositoryImpl::class)
                .singletonInScope()
        bind(QuranImagesRepository::class)
                .toType(QuranImagesRepositoryImpl::class)
                .singletonInScope()
        bind(WordByWordTranslationsRepository::class)
                .toType(WordByWordTranslationsRepositoryImpl::class)
                .singletonInScope()
        bind(SurahsRepository::class)
                .toType(SurahsRepositoryImpl::class)
                .singletonInScope()
        bind(QuranPageRepository::class)
                .toType(QuranPageRepositoryImpl::class)
                .singletonInScope()
    }

}