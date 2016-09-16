# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in E:\czeal\softwares\android-sdk-windows/tools/proguard/proguard-android.txt
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

# bugly 2.2.0
# https://bugly.qq.com/androidfast
-dontwarn com.tencent.bugly.**
-keep public class com.tencent.bugly.**{*;}

# stetho 1.3.1
# https://github.com/facebook/stetho/wiki/FAQ
-keep class com.facebook.stetho.** {
  *;
}

# rxjava 1.1.8
# https://github.com/artem-zinnatullin/RxJavaProGuardRules/blob/master/rxjava-proguard-rules/proguard-rules.txt

-dontwarn sun.misc.**

-keepclassmembers class rx.internal.util.unsafe.*ArrayQueue*Field* {
   long producerIndex;
   long consumerIndex;
}

-keepclassmembers class rx.internal.util.unsafe.BaseLinkedQueueProducerNodeRef {
    rx.internal.util.atomic.LinkedQueueNode producerNode;
}

-keepclassmembers class rx.internal.util.unsafe.BaseLinkedQueueConsumerNodeRef {
    rx.internal.util.atomic.LinkedQueueNode consumerNode;
}

# gson 2.7
# https://github.com/google/gson/blob/master/examples/android-proguard-example/proguard.cfg
-keepattributes Signature

# For using GSON @Expose annotation
-keepattributes *Annotation*

# Gson specific classes
-keep class sun.misc.Unsafe { *; }
#-keep class com.google.gson.stream.** { *; }

# Application classes that will be serialized/deserialized over Gson
-keep class com.google.gson.examples.android.model.** { *; }

# glide 3.7.0
# https://github.com/bumptech/glide#proguard
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep public enum com.bumptech.glide.load.resource.bitmap.ImageHeaderParser$** {
  **[] $VALUES;
  public *;
}
# for DexGuard only
#-keepresourcexmlelements manifest/application/meta-data@value=GlideModule

# retrofit 2.1.0
# https://square.github.io/retrofit/
# Platform calls Class.forName on types which do not exist on Android to determine platform.
-dontnote retrofit2.Platform
# Platform used when running on RoboVM on iOS. Will not be used at runtime.
-dontnote retrofit2.Platform$IOS$MainThreadExecutor
# Platform used when running on Java 8 VMs. Will not be used at runtime.
-dontwarn retrofit2.Platform$Java8
# Retain generic type information for use by reflection by converters and adapters.
-keepattributes Signature
# Retain declared checked exceptions for use by a Proxy instance.
-keepattributes Exceptions

# okhttp 3.4.1
# https://github.com/krschultz/android-proguard-snippets/blob/master/libraries/proguard-square-okhttp3.pro
-keepattributes Signature
-keepattributes *Annotation*
-keep class okhttp3.** { *; }
-keep interface okhttp3.** { *; }
-dontwarn okhttp3.**

# okio
# https://github.com/krschultz/android-proguard-snippets/blob/master/libraries/proguard-square-okio.pro
-keep class sun.misc.Unsafe { *; }
-dontwarn java.nio.file.*
-dontwarn org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement
-dontwarn okio.**

# eventbus 3.0.0
# http://greenrobot.org/eventbus/documentation/proguard/
-keepattributes *Annotation*
-keepclassmembers class ** {
    @org.greenrobot.eventbus.Subscribe <methods>;
}
-keep enum org.greenrobot.eventbus.ThreadMode { *; }

# Only required if you use AsyncExecutor
-keepclassmembers class * extends org.greenrobot.eventbus.util.ThrowableFailureEvent {
    <init>(java.lang.Throwable);
}

# support-v7-appcompat
# https://github.com/krschultz/android-proguard-snippets/blob/master/libraries/proguard-support-v7-appcompat.pro
-keep public class android.support.v7.widget.** { *; }
-keep public class android.support.v7.internal.widget.** { *; }
-keep public class android.support.v7.internal.view.menu.** { *; }

-keep public class * extends android.support.v4.view.ActionProvider {
    public <init>(android.content.Context);
}

# support-design
# https://github.com/krschultz/android-proguard-snippets/blob/master/libraries/proguard-support-design.pro
-dontwarn android.support.design.**
-keep class android.support.design.** { *; }
-keep interface android.support.design.** { *; }
-keep public class android.support.design.R$* { *; }

# support-v7-cardview
# https://github.com/krschultz/android-proguard-snippets/blob/master/libraries/proguard-support-v7-cardview.pro
# http://stackoverflow.com/questions/29679177/cardview-shadow-not-appearing-in-lollipop-after-obfuscate-with-proguard/29698051
-keep class android.support.v7.widget.RoundRectDrawable { *; }

# sqlite
# https://github.com/krschultz/android-proguard-snippets/blob/master/libraries/proguard-sqlite.pro
-keep class org.sqlite.** { *; }
-keep class org.sqlite.database.** { *; }

# lspush
-keep public class com.tomeokin.lspush.data.model.**

# mob sms sdk
-keepattributes EnclosingMethod
-keep class cn.smssdk.** { *; }
-keep class com.mob.commpns.** { *; }

# alibaba jaq sdk
-keep class com.taobao.securityjni.**{*;}
-keep class com.taobao.wireless.security.**{*;}
-keep class com.ut.secbody.**{*;}
-keep class com.taobao.dp.**{*;}
-keep class com.alibaba.wireless.security.**{*;}