package org.quranacademy.quran.appinitializer.initializers

import android.content.Context
import com.google.gson.FieldNamingPolicy
import com.google.gson.GsonBuilder
import com.mta.tehreer.graphics.Typeface
import com.mta.tehreer.graphics.TypefaceManager
import org.quranacademy.quran.appinitializer.AppInitializerElement
import org.quranacademy.quran.core.ui.R
import org.quranacademy.quran.data.AssetsFileExtractor
import org.quranacademy.quran.data.prefs.GeneralPreferences
import java.io.File
import javax.inject.Inject

class TehreerLibraryInitializer @Inject constructor(
        private val preferences: GeneralPreferences,
        private val assetsFileExtractor: AssetsFileExtractor
) : AppInitializerElement {

    private val gson = GsonBuilder()
            .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
            .create()

    override fun initialize(context: Context) {
        val currentFonts = listOf(
                R.string.font_uthmanic_hafs to 2,
                R.string.font_uthmanic_hafs_old to 2
        ).map {
            val fontPathResId = it.first
            val fontPath = context.getString(it.first)
            val fontFileName = fontPath.substringAfterLast("/")
            NewFontInfo(fontPathResId, fontFileName, it.second)
        }
        val oldFontsInfo = getOldFontsInfo()
        val fontsFolder = File(context.filesDir, "fonts")

        deleteOldFonts(fontsFolder, currentFonts)
        extractNewFonts(fontsFolder, currentFonts, oldFontsInfo)
        saveFontsInfoToPrefs(currentFonts)
    }

    private fun getOldFontsInfo(): List<OldFontInfo> {
        val fontsInfoJson = preferences.getString(PREF_FIELD_NAME, null)
        return if (fontsInfoJson != null) {
            gson.fromJson(fontsInfoJson, Array<OldFontInfo>::class.java).toList()
        } else {
            emptyList()
        }
    }

    private fun extractNewFonts(
            fontsFolder: File,
            currentFonts: List<NewFontInfo>,
            oldFontsInfo: List<OldFontInfo>
    ) {
        currentFonts.forEach { newFont ->
            val oldFont = oldFontsInfo.firstOrNull { it.name == newFont.name }
            val isFontUpdated = oldFont != null && oldFont.version < newFont.version

            val extractedFile = File(fontsFolder, newFont.name)
            val assetsPath = "fonts/${newFont.name}"
            assetsFileExtractor.extract(assetsPath, extractedFile, isFontUpdated)

            val typeface = Typeface(extractedFile)
            TypefaceManager.registerTypeface(typeface, newFont.fontPathResId)
        }
    }

    private fun deleteOldFonts(
            fontsFolder: File,
            currentFonts: List<NewFontInfo>
    ) {
        val currentFontNames = currentFonts.map { it.name }
        fontsFolder.list()?.forEach { existingFont ->
            val existingFontFile = File(fontsFolder, existingFont)
            val isFontDeleted = !currentFontNames.contains(existingFont)
            if (isFontDeleted) {
                existingFontFile.delete()
            }
        }
    }

    private fun saveFontsInfoToPrefs(currentFonts: List<NewFontInfo>) {
        val newFontsData = currentFonts.map { OldFontInfo(it.name, it.version) }
        preferences.getString(PREF_FIELD_NAME, gson.toJson(newFontsData))
    }

    class NewFontInfo(val fontPathResId: Int, val name: String, val version: Int)

    class OldFontInfo(val name: String, val version: Int)

    companion object {
        val PREF_FIELD_NAME = "fonts_info"
    }

}