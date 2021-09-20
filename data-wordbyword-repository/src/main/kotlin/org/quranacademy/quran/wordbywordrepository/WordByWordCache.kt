package org.quranacademy.quran.wordbywordrepository

import org.quranacademy.quran.domain.models.AyahWordItem
import org.quranacademy.quran.domain.models.WordByWordTranslation
import javax.inject.Inject

class WordByWordCache @Inject constructor() {

    private var surahNumber: Int? = null
    private var translation: WordByWordTranslation? = null
    private lateinit var surahWordByWord: List<List<AyahWordItem>>

    fun isWordsCached(surahNumber: Int, translation: WordByWordTranslation) =
            this.surahNumber == surahNumber &&
                    this.translation != null &&
                    this.translation == translation

    fun saveToCache(surahNumber: Int, translation: WordByWordTranslation, surahWordByWord: List<List<AyahWordItem>>) {
        this.surahNumber = surahNumber
        this.translation = translation
        this.surahWordByWord = surahWordByWord
    }

    fun getSurahWordByWord() = surahWordByWord

    fun getAyahWordByWord(ayahNumber: Int) = surahWordByWord[ayahNumber - 1]

}