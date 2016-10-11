package com.bcgtgjyb.multidextool.tool;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.ComponentInfo;
import android.os.Build;
import android.os.Process;
import android.preference.Preference;
import android.support.multidex.MultiDex;
import android.util.Log;

import java.io.File;
import java.util.Map;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by bigwen on 16/10/11.
 */

public class DexLoadUtil {

    public static final String TAG = "muldex";
    public static final String PROCESS_NAME = ":multidex";
    private static final String DEX2_NAME = "classes2.dex";
    private static final String DEX_LOAD_PREF_KEY = "dex_load_pref_key";
    private static final String PREF_NAME = "multidex_pref";

    public static void attachBaseContext(Context base, int pid) {
        if (isDexLoadProcess(base, pid)) return;
        if (!isVMMultiDexCapable()) {
            if (!isLoadSuccess(base)) {
                loadDex(base);
            }
            long startTime = System.currentTimeMillis();
            MultiDex.install(base);
            Log.i(TAG, "time = " + (System.currentTimeMillis() - startTime));
        }
    }

    public static boolean isDexLoadProcess(Context context, int pid) {
        boolean isLoading = getProcessName(context, pid).contains(PROCESS_NAME);
        Log.i(TAG, "isDexLoadProcess: "+isLoading);
        return isLoading;
    }

    public static boolean isLoadSuccess(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        String sha1 = getDex2SHA1(context);
        String dexValue = sharedPreferences.getString(DEX_LOAD_PREF_KEY, "123");
        boolean isLoadSuccess = sha1.equals(dexValue);
        Log.i(TAG, "isLoadSuccess: "+isLoadSuccess);
        return isLoadSuccess;
    }

    public static void finishDexLoad(Application application) {
        Log.i(TAG, "finishDexLoad: ");
        SharedPreferences sharedPreferences = application.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        sharedPreferences.edit().putString(DEX_LOAD_PREF_KEY, getDex2SHA1(application)).apply();
    }

    private static String getProcessName(Context context, int pid) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningAppProcessInfo appProcessInfo : activityManager.getRunningAppProcesses()) {
            if (appProcessInfo.pid == pid) {
                String name = appProcessInfo.processName;
                Log.i(TAG, "getProcessName: "+name);
                return name;
            }
        }
        return "";
    }

    private static String getDex2SHA1(Context context) {
        ApplicationInfo applicationInfo = context.getApplicationInfo();
        String path = applicationInfo.sourceDir;
        try {
            JarFile jarFile = new JarFile(path);
            Manifest manifest = jarFile.getManifest();
            Map<String, Attributes> map = manifest.getEntries();
            Attributes attributes = map.get(DEX2_NAME);
            return attributes.getValue("SHA1-Digest");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    private static void loadDex(Context context) {
        Log.i(TAG, "loadDex: ");
        Intent intent = new Intent();
        ComponentName componentName = new ComponentName(context.getPackageName(), DexLoadActivity.class.getName());
        intent.setComponent(componentName);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);

        long startTime = System.currentTimeMillis();
        long totalTime = 10 * 1000;
        while (!isLoadSuccess(context)) {
            try {
                long waitTime = System.currentTimeMillis() - startTime;
                if (waitTime > totalTime) {
                    break;
                }
                Thread.sleep(300);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static boolean isVMMultiDexCapable() {
        boolean isMultiDexCapable = false;
        String versionString = System.getProperty("java.vm.version");
        if (versionString != null) {
            Matcher matcher = Pattern.compile("(\\d+)\\.(\\d+)(\\.\\d+)?").matcher(versionString);
            if (matcher.matches()) {
                try {
                    int e = Integer.parseInt(matcher.group(1));
                    int minor = Integer.parseInt(matcher.group(2));
                    isMultiDexCapable = e > 2 || e == 2 && minor >= 1;
                } catch (NumberFormatException ignore) {
                }
            }
        }
        Log.i(TAG, "isVMMultiDexCapable: "+isMultiDexCapable);
        return isMultiDexCapable;
    }

}
