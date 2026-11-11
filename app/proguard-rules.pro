# Keep Room-generated code
-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Entity class *

# Moshi reflection-based adapters need the data classes kept
-keep class com.cyberboyone.lecturenotes.data.remote.*Dto { *; }
-keep class com.cyberboyone.lecturenotes.data.model.** { *; }
-keepclassmembers class com.cyberboyone.lecturenotes.data.model.** { *; }

# AdMob / Play Services
-keep class com.google.android.gms.ads.** { *; }

# PDF Viewer
-keep class com.github.barteksc.pdfviewer.** { *; }
-keep class com.shockwave.** { *; }

# Coil image loading
-keep class coil.** { *; }

# Supabase / OkHttp / Retrofit
-keepattributes Signature, InnerClasses, EnclosingMethod
-dontwarn okhttp3.**
-dontwarn retrofit2.**
