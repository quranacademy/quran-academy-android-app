package org.quranacademy.quran.presentation

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.fragment.app.DialogFragment
import me.aartikov.alligator.Screen
import me.aartikov.alligator.ScreenResult
import me.aartikov.alligator.converters.OneWayIntentConverter
import me.aartikov.alligator.navigationfactories.RegistryNavigationFactory
import org.quranacademy.quran.audiomanager.ui.recitationinfo.RecitationInfoActivity
import org.quranacademy.quran.audiomanager.ui.recitersslist.AudioManagerActivity
import org.quranacademy.quran.ayahdetails.presentation.AyahDetailsDialog
import org.quranacademy.quran.bookmarks.ui.BookmarkFoldersDialog
import org.quranacademy.quran.bookmarks.ui.BookmarkFoldersScreen
import org.quranacademy.quran.feedback.ui.FeedbackDialog
import org.quranacademy.quran.feedback.ui.RateAppDialog
import org.quranacademy.quran.languagesmanager.presentation.ui.LanguagesManagerActivity
import org.quranacademy.quran.mainscreen.presentation.ui.MainActivity
import org.quranacademy.quran.mainscreen.presentation.ui.jumptoayah.JumpToAyahDialog
import org.quranacademy.quran.memorization.routing.*
import org.quranacademy.quran.memorization.ui.MemorizationActivity
import org.quranacademy.quran.memorization.ui.help.MemorizationTutorialActivity
import org.quranacademy.quran.memorization.ui.options.MemorizationOptionsActivity
import org.quranacademy.quran.memorization.ui.results.MemorizationResultsActivity
import org.quranacademy.quran.memorization.ui.test.MemorizationTestActivity
import org.quranacademy.quran.mushaf.presentation.ui.MushafActivity
import org.quranacademy.quran.player.presentation.playercontrol.dynamicsettings.PlayerDynamicSettingsDialog
import org.quranacademy.quran.player.presentation.playercontrol.dynamicsettings.PlayerDynamicSettingsScreen
import org.quranacademy.quran.player.presentation.playeroptions.ayahselection.AyahSelectionDialog
import org.quranacademy.quran.player.presentation.playeroptions.reciterselection.RecitationSelectionDialog
import org.quranacademy.quran.presentation.mvp.routing.screens.*
import org.quranacademy.quran.presentation.textsettingspanel.QuranTextSettingsActivity
import org.quranacademy.quran.radio.presentation.start.StartRadioDialog
import org.quranacademy.quran.search.presentation.SearchActivity
import org.quranacademy.quran.settings.presentation.ui.SettingsActivity
import org.quranacademy.quran.settings.presentation.ui.mushaftype.MushafTypeSelectionActivity
import org.quranacademy.quran.sharingdialog.TranslationsSharingDialog
import org.quranacademy.quran.sharingdialog.TranslationsSharingScreen
import org.quranacademy.quran.splash.presentation.ui.intro.AppIntroActivity
import org.quranacademy.quran.splash.presentation.ui.splash.SplashActivity
import org.quranacademy.quran.surahdetails.ui.SurahDetailsActivity
import org.quranacademy.quran.tajweedrules.ui.TajweedRulesActivity
import org.quranacademy.quran.translationsmanager.presentation.ui.translationordering.TranslationsOrderActivity
import org.quranacademy.quran.translationsmanager.presentation.ui.translationsmanager.TranslationsManagerActivity
import org.quranacademy.quran.updateneeded.presentation.ui.UpdateNeededActivity
import org.quranacademy.quran.wordbywordmanager.presentation.ui.WordByWordTranslationsManagerActivity


class QuranAcademyNavigationFactory : RegistryNavigationFactory() {

    init {
        bindScreen<SplashScreen, SplashActivity>()
        bindScreen<IntroScreen, AppIntroActivity>()
        bindScreen<TajweedRulesScreen, TajweedRulesActivity>()
        bindScreen<UpdateNeededScreen, UpdateNeededActivity>()
        bindScreen<AudioManagerScreen, AudioManagerActivity>()
        bindScreen<RecitationInfoScreen, RecitationInfoActivity>()
        bindDialog<RateAppScreen, RateAppDialog>()
        bindDialog<FeedbackScreen, FeedbackDialog>()
        bindDialog<RadioScreen, StartRadioDialog>()

        bindScreen<MainScreen, MainActivity>()
        bindDialog<JumpToAyahScreen, JumpToAyahDialog>()
        bindScreen<SurahDetailsScreen, SurahDetailsActivity>()
        bindResultDialog<BookmarkFoldersScreen, BookmarkFoldersDialog, BookmarkFoldersScreen.Result>()
        bindDialog<AyahDetailsScreen, AyahDetailsDialog>()
        bindScreen<MushafScreen, MushafActivity>()
        bindResultDialog<PlayerDynamicSettingsScreen, PlayerDynamicSettingsDialog, PlayerDynamicSettingsScreen.Result>()
        bindScreen<SearchScreen, SearchActivity>()
        bindResultDialog<TranslationsSharingScreen, TranslationsSharingDialog, TranslationsSharingScreen.Result>()

        bindScreen<MemorizationOptionsScreen, MemorizationOptionsActivity>()
        bindScreen<MemorizationScreen, MemorizationActivity>()
        bindScreen<MemorizationTestScreen, MemorizationTestActivity>()
        bindScreen<MemorizationResultsScreen, MemorizationResultsActivity>()
        bindScreen<MemorizationTutorialScreen, MemorizationTutorialActivity>()

        bindResultDialog<AyahSelectionScreen, AyahSelectionDialog, AyahSelectionScreen.Result>()
        bindResultDialog<RecitationSelectionScreen, RecitationSelectionDialog, RecitationSelectionScreen.Result>()

        bindScreen<SettingsScreen, SettingsActivity>()
        bindScreen<MushafPageTypeSelectionScreen, MushafTypeSelectionActivity>()
        bindScreen<LanguagesManagerScreen, LanguagesManagerActivity>()
        bindScreen<QuranTextSettingsScreen, QuranTextSettingsActivity>()
        bindScreen<TranslationsManagerScreen, TranslationsManagerActivity>()
        bindScreen<TranslationsOrderScreen, TranslationsOrderActivity>()
        bindScreen<WordByWordTranslationsManagerScreen, WordByWordTranslationsManagerActivity>()

        val googlePlayConverter = object : OneWayIntentConverter<OpenGooglePlayScreen>() {
            override fun createIntent(context: Context, screen: OpenGooglePlayScreen): Intent {
                val appPackageName = context.packageName
                val uri = if (screen.openUrl) {
                    Uri.parse("https://play.google.com/store/apps/details?id=$appPackageName")
                } else {
                    Uri.parse("market://details?id=$appPackageName")
                }
                return Intent(Intent.ACTION_VIEW, uri)
            }
        }
        registerActivity(OpenGooglePlayScreen::class.java, googlePlayConverter)

        val appInfoScreenConverter = object : OneWayIntentConverter<AppInfoScreen>() {
            override fun createIntent(context: Context, screen: AppInfoScreen): Intent {
                //Open the specific App Info page:
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                intent.data = Uri.parse("package:" + context!!.packageName)
                return if (intent.resolveActivity(context.packageManager) != null) {
                    intent
                } else {
                    Intent(Settings.ACTION_MANAGE_APPLICATIONS_SETTINGS)
                }
            }
        }

        registerActivity(AppInfoScreen::class.java, appInfoScreenConverter)
    }

    inline fun <reified S : Screen, reified A : Activity> bindScreen() = registerActivity(S::class.java, A::class.java)

    inline fun <reified S : Screen, reified D : DialogFragment> bindDialog() = registerDialogFragment(S::class.java, D::class.java)

    inline fun <reified S : Screen, reified D : DialogFragment, reified R : ScreenResult> bindResultDialog() =
            registerDialogFragmentForResult(S::class.java, D::class.java, R::class.java)

}