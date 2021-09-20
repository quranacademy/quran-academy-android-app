# Add ayah specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html


##---------------Begin: Quran Academy app  ----------

##---------------End: Quran Academy app  ----------

# Firebase
-keepattributes *Annotation*
-keepattributes SourceFile,LineNumberTable
-keep public class * extends java.lang.Exception
-keep class com.crashlytics.** { *; }
-dontwarn com.crashlytics.**

# Toothpick

-keep class **__Factory { *; }
-keep class **__MemberInjector { *; }

# Note that if we could use kapt to generate registries, possible to get rid of this
-keepattributes *Annotation*
# Do not obfuscate classes with Injected Constructors
-keepclasseswithmembernames class * { @javax.inject.Inject <init>(...); }
# Do not obfuscate classes with Injected Fields
-keepclasseswithmembernames class * { @javax.inject.Inject <fields>; }
# Do not obfuscate classes with Injected Methods
-keepclasseswithmembernames class * { @javax.inject.Inject <methods>; }
-keep @android.support.annotation.Keep class *
-dontwarn javax.inject.**
-dontwarn javax.annotation.**
-keep class **__Factory { *; }
-keep class **__MemberInjector { *; }

-adaptclassstrings
-keep class toothpick.** { *; }

-keep @javax.inject.Singleton class *

# DBFlow

-keep class * extends com.raizlabs.android.dbflow.config.DatabaseHolder { *; }

# Tehreer Androi

-keepclassmembers, includedescriptorclasses class * {
    native <methods>;
}

-keep @interface com.mta.tehreer.internal.Sustain

-keepclassmembers class * {
    @com.mta.tehreer.internal.Sustain *;
}

# OkHttp

-dontwarn okhttp3.**
-dontwarn okio.**
-dontwarn javax.annotation.**
-dontwarn org.conscrypt.**
# A resource is loaded with a relative path so the package of this class must be preserved.
-keepnames class okhttp3.internal.publicsuffix.PublicSuffixDatabase

# Retrofit

-dontnote retrofit2.Platform
-dontwarn retrofit2.Platform$Java8
-keepattributes Signature
-keepattributes Exceptions

# Okio
-dontwarn okio.**

# OkDownload
# A resource is loaded with a relative path so the package of this class must be preserved.
-keepnames class okhttp3.internal.publicsuffix.PublicSuffixDatabase
-dontwarn com.liulishuo.okdownload.**

# JodaTime

-dontwarn org.joda.convert.**
-dontwarn org.joda.time.**
-keep class org.joda.time.** { *; }
-keep interface org.joda.time.** { *; }

# Gson

# Gson uses generic type information stored in a class file when working with fields. Proguard
# removes such information by default, so configure it to keep all of it.
-keepattributes Signature

# For using GSON @Expose annotation
-keepattributes *Annotation*

# Gson specific classes
-dontwarn sun.misc.**
#-keep class com.google.gson.stream.** { *; }

# Prevent proguard from stripping interface information from TypeAdapterFactory,
# JsonSerializer, JsonDeserializer instances (so they can be used in @JsonAdapter)
-keep class * implements com.google.gson.TypeAdapterFactory
-keep class * implements com.google.gson.JsonSerializer
-keep class * implements com.google.gson.JsonDeserializer