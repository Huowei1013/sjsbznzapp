# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in D:\Android\sdk/tools/proguard/proguard-android.txt
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

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

-keepattributes Exceptions,InnerClasses,SourceFile,LineNumberTable
-keepattributes Signature
-keepattributes *Annotation*
-keep class **.R$* {*;}
-keep class android.support.**{*;}
-keep interface android.support.** { *; }

#com.beagle
-keep  class * implements com.beagle.component.app.IApplication
-keep  class * extends com.beagle.component.app.MvpPresenter

-keep public class com.beagle.xssjrdapp.R$*{
public static final int *;
}

-keep class com.bysj.znzapp.bean.**{*;}
-keep class com.bysj.znzapp.greendao.gen.**{*;}

#views
-keep class com.bysj.znzapp.views.**{*;}

#需要反射获取类方法名
#-keep class com.bysj.znzapp.data.PeopleDataOperation{ *; }

#js
-keepattributes *JavascriptInterface*
-keep class android.webkit.JavascriptInterface {*;}

#okhttputils
-dontwarn com.beagle.okhttp.**
-keep class com.beagle.okhttp.**{*;}

#百度地图
-keep class com.baidu.** {*;}
-keep class vi.com.** {*;}
-keep class com.baidu.vi.** {*;}
-dontwarn com.baidu.**
-keep class mapsdkvi.** {*;}
-dontwarn mapsdkvi.**

#百度地图 Track
-keep class com.beagle.component.app.ModuleApplicaiton{*;}

#tencent
-keep class com.tencent.**{*;}

-keep class sun.misc.Unsafe{*;}

#okhttp
-dontwarn okhttp3.**
-keep class okhttp3.**{*;}

#okio
-dontwarn okio.**
-keep class okio.**{*;}

#gson
-dontwarn com.google.**
-keep class com.google.**
-keep class com.google.gson.examples.android.model.**{*;}

#保持 native 方法不被混淆
-keepclasseswithmembernames class * {
    native <methods>;
}

# 保留Parcelable序列化类不被混淆
-keep class * implements android.os.Parcelable {
    public static final android.os.Parcelable$Creator *;
}

# Retrolambda
-dontwarn java.lang.invoke.*

#greenDao
-dontwarn org.greenrobot.greendao.database.**
-keep class de.greenrobot.dao.** {*;}
-keepclassmembers class * extends de.greenrobot.dao.AbstractDao {
    public static Java.lang.String TABLENAME;
}
-keep class **$Properties

#Rxjava
-dontwarn rx.**

#bugly
-dontwarn com.tencent.bugly.**
-keep public class com.tencent.bugly.**{*;}
-dontwarn com.tencent.tinker.**
-keep class com.tencent.tinker.** { *; }

#手动启用support keep注解
-dontskipnonpubliclibraryclassmembers
-printconfiguration
-keep,allowobfuscation @interface android.support.annotation.Keep

-keep @android.support.annotation.Keep class *
-keepclassmembers class * {
    @android.support.annotation.Keep *;
}

#腾讯直播
-keep class com.tencent.**{*;}
-dontwarn com.tencent.**

-keep class tencent.**{*;}
-dontwarn tencent.**

-keep class qalsdk.**{*;}
-dontwarn qalsdk.**

#极光推送
-dontoptimize
-dontpreverify

-dontwarn cn.jpush.**
-keep class cn.jpush.**{*;}

-dontwarn cn.jiguang.**
-keep class cn.jiguang.**{*;}

#protobuf
-keep class com.google.protobuf.**{*;}

#umeng
-keepclassmembers class * {
   public <init> (org.json.JSONObject);
}

-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

-dontwarn com.umeng.analytics.**
-keep class com.umeng.analytics.**{*;}

#videoPlay
-keep class tv.danmaku.ijk.** { *; }
-dontwarn tv.danmaku.ijk.**
-keep class com.shuyu.gsyvideoplayer.** { *; }
-dontwarn com.shuyu.gsyvideoplayer.**

#视频监控
-keep class com.kilo.** { *; }
-dontwarn com.kilo.**

-keep class com.hik.** { *; }
-dontwarn com.hik.**

-keep class com.mobile.** { *; }
-dontwarn com.mobile.**

-keep class org.MediaPlayer.** { *; }
-dontwarn org.MediaPlayer.**

-keep class com.hikvision.** { *; }
-dontwarn com.hikvision.**

-keep class com.lidroid.** { *; }
-dontwarn com.lidroid.**

-keep class com.topsec.sslvpn.** { *; }
-dontwarn com.topsec.sslvpn.**

#Arouters#
-keep public class com.alibaba.android.arouter.routes.**{*;}
-keep public class com.alibaba.android.arouter.facade.**{*;}
-keep class * implements com.alibaba.android.arouter.facade.template.ISyringe{*;}
-dontwarn com.alibaba.android.arouter.**
# If you use the byType method to obtain Service, add the following rules to protect the interface:
-keep interface * implements com.alibaba.android.arouter.facade.template.IProvider
# If single-type injection is used, that is, no interface is defined to implement IProvider, the following rules need to be added to protect the implementation
-keep class * implements com.alibaba.android.arouter.facade.template.IProvider

#Butterknife#
-keep class butterknife.** { *; }
-dontwarn butterknife.internal.**
-keep class **$$ViewBinder { *; }
-keepclasseswithmembernames class * {
    @butterknife.* <fields>;
}
-keepclasseswithmembernames class * {
    @butterknife.* <methods>;
}