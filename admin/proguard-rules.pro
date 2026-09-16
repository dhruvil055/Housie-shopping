# ProGuard Rules for Housie Admin App

# Keep Admin Models and DTOs
-keep class com.housieshopping.admin.domain.model.** { *; }
-keep class com.housieshopping.admin.data.** { *; }
-keep class com.housieshopping.admin.core.network.** { *; }

# Retrofit & OkHttp
-keepattributes Signature, InnerClasses, EnclosingMethod
-keepattributes RuntimeVisibleAnnotations, RuntimeVisibleParameterAnnotations
-keepclassmembers,allowobfuscation interface * {
    @retrofit2.http.* <methods>;
}
-dontwarn retrofit2.**
-dontwarn okhttp3.**
-dontwarn okio.**

# Gson
-keepattributes *Annotation*
-keepclassmembers class * {
    @com.google.gson.annotations.SerializedName <fields>;
}

# Room Database
-keepclassmembers class * extends androidx.room.RoomDatabase {
    <init>();
}
-keep class * extends androidx.room.RoomDatabase
-dontwarn androidx.room.paging.**

# Dagger Hilt
-keep class * extends dagger.hilt.internal.GeneratedComponent { *; }
-keep class dagger.hilt.** { *; }
-dontwarn dagger.hilt.**

# Coil Image Loader
-keep class coil.** { *; }
-dontwarn coil.**
