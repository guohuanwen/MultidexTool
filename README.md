# MultidexTool  
google multidex方案优化  

#使用：  
1，gradle配置  
```gradle
apply plugin: 'com.android.application'

android {
    ...
    defaultConfig {
        ...
        multiDexEnabled true
        ...
    }
    ...
}

dependencies {
    ...
    compile 'com.android.support:multidex:1.0.1'
}
```
1，DexLoadActivity.java，DexLoadUtil.java这两个类copy到自己的项目目录  
2，AndroidManifest.xml中配置  
```xml
    <application
        android:name=".MyApplication"
        android:allowBackup="true"
        android:icon="@mipmap/ic_launcher"
        android:label="@string/app_name"
        android:supportsRtl="true"
        android:theme="@style/AppTheme">
        
        <!--对应DexLoadActivity所在的目录-->
        <activity android:name=".tool.DexLoadActivity"
            android:process=":multidex" />
        <!--对应DexLoadActivity所在的目录-->

    </application>

```
3，在自己的Application中添加下面代码  
```java
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        DexLoadUtil.attachBaseContext(base, android.os.Process.myPid());
        //next your code
    }

    @Override
    public void onCreate() {
        super.onCreate();
        if (DexLoadUtil.isDexLoadProcess(this, android.os.Process.myPid())) return;
        //next your code

    }
```
