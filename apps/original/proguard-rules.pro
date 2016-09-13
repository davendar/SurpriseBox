# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /usr/local/Cellar/android-sdk/24.4.1_1/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}



-optimizationpasses 5
-dontusemixedcaseclassnames
-dontskipnonpubliclibraryclasses
-dontskipnonpubliclibraryclassmembers
-dontpreverify
-verbose
-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*
-allowaccessmodification
-keepattributes *Annotation*
-renamesourcefileattribute SourceFile
-keepattributes SourceFile,LineNumberTable
-repackageclasses ''



# DAGGER
-dontwarn dagger.internal.codegen.**
-keep @dagger.Module class ** { *; }
-keep class * {
    @javax.inject.Inject <fields>;
}
-keepclassmembers,allowobfuscation class * {
    @javax.inject.* *;
    @dagger.* *;
    <init>();
}

-keep class javax.inject.** { *; }
-keep class **$$ModuleAdapter
-keep class **$$InjectAdapter
-keep class **$$StaticInjection
-keep class dagger.** { *; }


# PICASSO
-dontwarn com.squareup.okhttp.**
-dontwarn okhttp3.**
-dontwarn okio.**

#INMOBI SDK

-keep class com.google.android.gms.common.api.GoogleApiClient { public *; }
-keep class com.google.android.gms.common.api.GoogleApiClient$* {public *;}
-keep class com.google.android.gms.location.LocationServices {public *;}
-keep class com.google.android.gms.location.FusedLocationProviderApi {public *;}
-keep class com.google.android.gms.location.ActivityRecognition {public *;}
-keep class com.google.android.gms.location.ActivityRecognitionApi {public *;}
-keep class com.google.android.gms.location.ActivityRecognitionResult {public *;}
-keep class com.google.android.gms.location.DetectedActivity {public *;}
-keep class com.google.android.gms.ads.identifier.AdvertisingIdClient{public *;}
-keep class com.google.android.gms.ads.identifier.AdvertisingIdClient$Info{public *;}


-keepattributes SourceFile,LineNumberTable,InnerClasses


# JavascriptInterface annotations
-keepnames @android.webkit.JavascriptInterface class *
-keepclassmembernames class * {
    @android.webkit.JavascriptInterface *;
}
-keepclassmembers class ** {
    @android.webkit.JavascriptInterface *;
}


-dontwarn com.google.android.gms**

-keepattributes Signature

# For using GSON @Expose annotation
-keepattributes *Annotation*

# Gson specific classes
-keep class sun.misc.Unsafe { *; }
#-keep class com.google.gson.stream.** { *; }






