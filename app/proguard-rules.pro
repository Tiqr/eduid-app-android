# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

-keep public class * extends androidx.lifecycle.ViewModel
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class nl.eduid.MainComposeActivity

-keepattributes *Annotation*
-keepclassmembers class * implements android.os.Parcelable {
    static ** CREATOR;
}
# keep names for any Parcelabe & Serializable
-keepnames class * extends android.os.Parcelable
-keepnames class * extends java.io.Serializable