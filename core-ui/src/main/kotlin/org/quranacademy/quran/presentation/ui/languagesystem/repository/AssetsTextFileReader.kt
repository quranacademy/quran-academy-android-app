package org.quranacademy.quran.presentation.ui.languagesystem.repository

import android.content.Context

class AssetsTextFileReader(private val context: Context) {

    fun readFile(filePath: String): String {
        var fileContent: String
        context.assets.open(filePath).apply {
            fileContent = this.readBytes().toString(Charsets.UTF_8)
        }.close()
        return fileContent
    }

}