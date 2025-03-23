# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Keep JavaMail API
-keep class javax.mail.** { *; }
-keep class javax.activation.** { *; }
-keep class com.sun.mail.** { *; }
-dontwarn javax.mail.**
-dontwarn javax.activation.**
-dontwarn com.sun.mail.**

# Keep classes that might be used via reflection
-keepclassmembers class * {
    @javax.mail.* *;
}

# General Android rules
-keepattributes *Annotation*
-keepattributes SourceFile,LineNumberTable
-keep public class * extends java.lang.Exception

# Keep the application class
-keep public class com.example.otpforwarder.OTPForwarderApp { *; }

# Keep the SMS receiver
-keep public class com.example.otpforwarder.OTPForwarderApp$SMSReceiver { *; }
