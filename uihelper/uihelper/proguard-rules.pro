# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /android-sdk-linux/tools/proguard/proguard-android.txt
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

-optimizationpasses 1
-dontpreverify
-verbose
-dontshrink
-keepparameternames
-renamesourcefileattribute SourceFile
#-keepattributes SourceFile,LineNumberTable,Exceptions,InnerClasses,Signature
-keepattributes Exceptions,InnerClasses,Signature,Deprecated,SourceFile,LineNumberTable,*Annotation*,EnclosingMethod
-optimizations !field/removal/writeonly,!field/marking/private,!class/merging/*,!code/allocation/variable,!code/simplification/arithmetic,!field/*
-dump dump.txt
-printmapping mapping.txt

-keep class butterknife.** { *; }
-dontwarn butterknife.internal.**
-keep class **$$ViewBinder { *; }

-keepclasseswithmembernames class * {
    @butterknife.* <fields>;
}

-keepclasseswithmembernames class * {
    @butterknife.* <methods>;
}

-keepclasseswithmembernames,includedescriptorclasses class * {
    native <methods>;
}

-keep class com.mesibo.uihelper.MesiboUiHelper {
    public static *;
    public *** init(...);
}

-keep class com.mesibo.uihelper.MesiboUiHelperConfig {*;}
-keep class com.mesibo.uihelper.ILoginInterface {*;}
-keep class com.mesibo.uihelper.ILoginResultsInterface {*;}
-keep class com.mesibo.uihelper.IProductTourListener {*;}
-keep class com.mesibo.uihelper.WelcomeScreen {*;}
-keep class com.mesibo.uihelper.PhoneAuthenticationHelper {*;}
-keep class com.mesibo.uihelper.PhoneAuthenticationHelper$PhoneNumber {*;}
-keep class com.mesibo.uihelper.PhoneAuthenticationHelper$Listener {*;}


-keep class android.support.v4.app.** { *; }
-keep interface android.support.v4.app.** { *; }

-keep class com.google.i18n.phonenumbers.** { *; }

##---------------Begin: proguard configuration for Gson  ----------
# Gson uses generic type information stored in a class file when working with fields. Proguard
# removes such information by default, so configure it to keep all of it.
-keepattributes Signature

# For using GSON @Expose annotation
-keepattributes *Annotation*

# Gson specific classes
-keep class sun.misc.Unsafe { *; }
#-keep class com.google.gson.stream.** { *; }

# Application classes that will be serialized/deserialized over Gson
-keep class com.google.gson.examples.android.model.** { *; }

##---------------End: proguard configuration for Gson  ----------