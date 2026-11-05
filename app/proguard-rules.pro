# Keep Room-generated code
-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Entity class *

# Moshi reflection-based adapters need the data classes kept
-keep class com.cyberboyone.lecturenotes.data.remote.*Dto { *; }
-keep class com.cyberboyone.lecturenotes.data.model.* { *; }

# AdMob / Play Services
-keep class com.google.android.gms.ads.** { *; }
