package org.quranacademy.quran.di.modules

import android.content.Context
import org.quranacademy.quran.ayahsrepository.AyahTranslationDatabaseMapper
import org.quranacademy.quran.ayahsrepository.AyahsDataSourceImpl
import org.quranacademy.quran.ayahsrepository.database.AyahsArabicDatabaseAdapterImpl
import org.quranacademy.quran.data.AyahsDataSource
import org.quranacademy.quran.data.RecitationsListDownloader
import org.quranacademy.quran.data.SurahsListDownloader
import org.quranacademy.quran.data.database.adapters.*
import org.quranacademy.quran.data.downloading.QuranEndpointsProvider
import org.quranacademy.quran.data.mushaf.MushafPageBoundsDataSource
import org.quranacademy.quran.data.mushaf.QuranImageFilesChecker
import org.quranacademy.quran.data.translations.TranslationsListDownloader
import org.quranacademy.quran.data.translations.WordByWordDataSource
import org.quranacademy.quran.data.translations.WordByWordTranslationsListDownloader
import org.quranacademy.quran.di.bind
import org.quranacademy.quran.di.toType
import org.quranacademy.quran.mushafpageboundsrepository.MushafPageBoundsDataSourceImpl
import org.quranacademy.quran.mushafpageboundsrepository.database.MushafPageBoundsDatabaseAdapterImpl
import org.quranacademy.quran.presentation.extensions.getScreenSize
import org.quranacademy.quran.quranimagesrepository.DisplaySize
import org.quranacademy.quran.quranimagesrepository.QuranImageDownloader
import org.quranacademy.quran.quranimagesrepository.QuranImageFilesCheckerImpl
import org.quranacademy.quran.quranimagesrepository.urlpovider.QuranUrlProviderImpl
import org.quranacademy.quran.recitationsrepository.recitations.RecitationsListDownloaderImpl
import org.quranacademy.quran.surahsrepository.SurahsListDownloaderImpl
import org.quranacademy.quran.translationsorder.TranslationsOrderManagerImpl
import org.quranacademy.quran.translationsrepository.TranslationsCache
import org.quranacademy.quran.translationsrepository.TranslationsDataSource
import org.quranacademy.quran.translationsrepository.TranslationsListDownloaderImpl
import org.quranacademy.quran.translationsrepository.database.TranslationDatabaseFileManagerImpl
import org.quranacademy.quran.translationsrepository.database.TranslationDatabaseManagerImpl
import org.quranacademy.quran.translationsrepository.database.TranslationDatabaseMapper
import org.quranacademy.quran.wordbywordrepository.WordByWordDataSourceImpl
import org.quranacademy.quran.wordbywordrepository.WordByWordTranslationsCache
import org.quranacademy.quran.wordbywordrepository.WordByWordTranslationsDataSource
import org.quranacademy.quran.wordbywordrepository.WordByWordTranslationsListDownloaderImpl
import org.quranacademy.quran.wordbywordrepository.database.WordByWordArabicDatabaseAdapterImpl
import org.quranacademy.quran.wordbywordrepository.database.WordByWordTranslationDatabaseFileManagerImpl
import org.quranacademy.quran.wordbywordrepository.database.WordByWordTranslationDatabaseManagerImpl
import toothpick.config.Module

class DataModule(context: Context) : Module() {

    init {
        bind(QuranEndpointsProvider::class)
                .toType(QuranUrlProviderImpl::class)
        bind(MushafPageBoundsDataSource::class)
                .toType(MushafPageBoundsDataSourceImpl::class)
                .singletonInScope()
        bind(MushafPageBoundsDatabaseAdapter::class)
                .toType(MushafPageBoundsDatabaseAdapterImpl::class)
                .singletonInScope()
        bind(AyahsDataSource::class)
                .toType(AyahsDataSourceImpl::class)
                .singletonInScope()
        bind(AyahsArabicDatabaseAdapter::class)
                .toType(AyahsArabicDatabaseAdapterImpl::class)
                .singletonInScope()
        bind(AyahTranslationDatabaseMapper::class)
                .singletonInScope()
        bind(TranslationDatabaseManager::class)
                .toType(TranslationDatabaseManagerImpl::class)
                .singletonInScope()
        bind(TranslationDatabaseFileManager::class)
                .toType(TranslationDatabaseFileManagerImpl::class)
                .singletonInScope()
        bind(QuranImageFilesChecker::class)
                .toType(QuranImageFilesCheckerImpl::class)
                .singletonInScope()
        bind(TranslationDatabaseManager::class)
                .toType(TranslationDatabaseManagerImpl::class)
                .singletonInScope()

        bind(TranslationsOrderManager::class)
                .toType(TranslationsOrderManagerImpl::class)
                .singletonInScope()

        bind(TranslationsCache::class).singletonInScope()
        bind(WordByWordTranslationsCache::class).singletonInScope()
        bind(TranslationDatabaseMapper::class).singletonInScope()
        bind(QuranImageDownloader::class).singletonInScope()
        bind(TranslationsDataSource::class).singletonInScope()
        bind(TranslationsListDownloader::class)
                .toType(TranslationsListDownloaderImpl::class)
                .singletonInScope()
        bind(WordByWordTranslationsListDownloader::class)
                .toType(WordByWordTranslationsListDownloaderImpl::class)
                .singletonInScope()

        bind(WordByWordTranslationDatabaseManager::class)
                .toType(WordByWordTranslationDatabaseManagerImpl::class)
                .singletonInScope()
        bind(WordByWordTranslationDatabaseFileManager::class)
                .toType(WordByWordTranslationDatabaseFileManagerImpl::class)
                .singletonInScope()

        bind(WordByWordTranslationsDataSource::class)
                .singletonInScope()
        bind(WordByWordDataSource::class)
                .toType(WordByWordDataSourceImpl::class)
                .singletonInScope()
        bind(WordByWordDatabaseAdapter::class)
                .toType(WordByWordArabicDatabaseAdapterImpl::class)
                .singletonInScope()

        bind(SurahsListDownloader::class)
                .toType(SurahsListDownloaderImpl::class)
                .singletonInScope()
        bind(RecitationsListDownloader::class)
                .toType(RecitationsListDownloaderImpl::class)
                .singletonInScope()

        val point = context.getScreenSize()
        bind(DisplaySize::class).toInstance(DisplaySize(point.x, point.y))
    }

}