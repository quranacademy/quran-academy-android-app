package org.quranacademy.quran.presentation.ui.languagesystem.fallback

object FallbacksTree {

    const val DEFAULT_LANGUAGE = "en"

    private val supportedLanguages = listOf(
            "ar", "av", "az", "ce",
            "de", "es", "fr", "inh",
            "kk", "lez", "ru", "tr",
            "tt", "uk"
    )

    private val fallbacks = mapOf(
            "am" to "en",
            "ar" to "en",
            "av" to "ru",
            "az" to "ru",
            "ba" to "ru",
            "bg" to "ru",
            "bn" to "en",
            "bs" to "en",
            "ce" to "ru",
            "cs" to "en",
            "de" to "en",
            "el" to "en",
            "es" to "en",
            "fa" to "ar",
            "fr" to "en",
            "ha" to "en",
            "he" to "en",
            "hi" to "en",
            "id" to "en",
            "inh" to "ru",
            "it" to "en",
            "ja" to "en",
            "ka" to "en",
            "kbd" to "ru",
            "kk" to "ru",
            "ko" to "en",
            "krc" to "ru",
            "ku" to "en",
            "lez" to "ru",
            "mo" to "ro",
            "ms" to "en",
            "ne" to "en",
            "nl" to "de",
            "no" to "en",
            "pl" to "en",
            "pt" to "en",
            "ro" to "en",
            "so" to "en",
            "sq" to "en",
            "sv" to "en",
            "sw" to "en",
            "ta" to "en",
            "tg" to "ru",
            "th" to "en",
            "tr" to "en",
            "tt" to "ru",
            "ug" to "en",
            "uk" to "ru",
            "ur" to "hi",
            "uz" to "ru",
            "zh" to "en"

    )

    fun isLanguageSupported(languageCode: String): Boolean = supportedLanguages.contains(languageCode)

    fun getFallbackFor(languageCode: String): String? {
        val fallbackCode = fallbacks[languageCode]
        if (fallbackCode != null) {
            return if (isLanguageSupported(fallbackCode)) {
                fallbackCode
            } else {
                getFallbackFor(fallbackCode)
            }
        }
        return null
    }

}