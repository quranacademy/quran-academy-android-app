package org.quranacademy.quran.player.data.quranplayer.playbackcontrollers.audiofocus.audiofocusmanager

import androidx.annotation.IntDef
import kotlin.annotation.AnnotationRetention.SOURCE

const val USAGE_MEDIA = 1
const val CONTENT_TYPE_MUSIC = 2

@IntDef(value = [USAGE_MEDIA])
@Retention(SOURCE)
annotation class Usage

@IntDef(value = [CONTENT_TYPE_MUSIC])
@Retention(SOURCE)
annotation class ContentType

data class AudioAttributesCompat(
        @Usage val usage: Int,
        @ContentType val contentType: Int,
        val legacyStreamType: Int
)