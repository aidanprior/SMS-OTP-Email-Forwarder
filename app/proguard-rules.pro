# ProGuard rules for the OTP Forwarder app

# Preserve the line number information for debugging stack traces
-keepattributes SourceFile,LineNumberTable

# Hide the original source file name
-renamesourcefileattribute SourceFile

# For native methods
-keepclasseswithmembernames class * {
    native <methods>;
}

# For JavaMail API
-keep class javax.** { *; }
-keep class com.sun.** { *; }
-keep class myjava.** { *; }
-keep class org.apache.harmony.** { *; }
-keep class com.example.otpforwarder.** { *; }
-keep public class com.sun.activation.registries.** { *; }
-keep public class com.sun.mail.dsn.** { *; }
-keep public class com.sun.mail.handlers.** { *; }
-keep public class com.sun.mail.smtp.** { *; }
-keep public class com.sun.mail.util.** { *; }
-keep public class javax.activation.** { *; }
-keep public class javax.mail.** { *; }

# Keep the BuildConfig
-keep class com.example.otpforwarder.BuildConfig { *; }

# Keep any custom exceptions
-keep public class * extends java.lang.Exception

# Keep the model/POJO classes
-keep class com.example.otpforwarder.detection.** { *; }

# For Android Support libraries
-keep class androidx.** { *; }
-keep interface androidx.** { *; }
-keep class com.google.android.material.** { *; }
-keep interface com.google.android.material.** { *; }

# For Lifecycle callbacks
-keepclassmembers class * implements androidx.lifecycle.LifecycleObserver {
    <init>(...);
}

# For preserving BroadcastReceiver
-keep class * extends android.content.BroadcastReceiver { *; }
-keep public class * implements android.os.Parcelable {
  public static final android.os.Parcelable$Creator *;
}

# For preserving Service
-keep class * extends android.app.Service

# Keep enum constants
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}
