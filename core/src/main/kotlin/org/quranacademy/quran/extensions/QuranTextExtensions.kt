package org.quranacademy.quran.extensions

fun String.normalizeHarfsInText(): String {
    return this
    return replace("\u0631\u064E\u0670", "\u0631\u064E\u200C\u0670")
            .replace("\u0632\u064E\u0670", "\u0632\u064E\u200C\u0670")
            .replace("\u062F\u064E\u0670", "\u062F\u064E\u200C\u0670")
            .replace("\u0630\u064E\u0670", "\u0630\u064E\u200C\u0670")
            .replace("\u0648\u064E\u0670", "\u0648\u064E\u200C\u0670")
            .replace("\u0631\u0651\u064E\u0670", "\u0631\u0651\u064E\u200B\u0670")
            .replace("\u0632\u0651\u064E\u0670", "\u0632\u0651\u064E\u200B\u0670")
            .replace("\u062F\u0651\u064E\u0670", "\u062F\u0651\u064E\u200B\u0670")
            .replace("\u0630\u0651\u064E\u0670", "\u0630\u0651\u064E\u200B\u0670")
            .replace("\u0648\u0651\u064E\u0670", "\u0648\u0651\u064E\u200B\u0670")
    //.replace("\u064E\u0670", "\u064E\u0640\u0670")
}