/*
 * Copyright 2016 TomeOkin
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.tomeokin.lspush.config;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;

import timber.log.Timber;

public class LsPushConfig {
    private static LsPushConfig sInstance;
    private static final String SERVER_URL = "LSPUSH_SERVER_URL";
    private static final String PUBLIC_KEY = "LSPUSH_PUBLIC_KEY";
    private static final String MOB_SMS_ID = "MOB_SMS_ID";
    private static final String MOB_SMS_KEY = "MOB_SMS_KEY";
    private static String serverUrl;
    private static String publicKey;
    private static String mobSMSId;
    private static String mobSMSKey;

    public static void init(Context context) {
        if (sInstance == null) {
            sInstance = new LsPushConfig(context.getApplicationContext());
        }
    }

    private LsPushConfig(Context context) {
        try {
            loadConfig(context);
        } catch (Exception e) {
            Timber.tag("app").w("loading config failure");
        }
    }

    public void loadConfig(Context context) throws PackageManager.NameNotFoundException {
        PackageManager packageManager = context.getPackageManager();
        ApplicationInfo appInfo =
                packageManager.getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
        if (appInfo != null && appInfo.metaData != null) {
            final Bundle metaData = appInfo.metaData;
            serverUrl = metaData.getString(SERVER_URL);
            publicKey = metaData.getString(PUBLIC_KEY);
            mobSMSId = metaData.getString(MOB_SMS_ID);
            mobSMSKey = metaData.getString(MOB_SMS_KEY);
        }
    }

    public static String getServerUrl() {
        return serverUrl;
    }

    public static String getPublicKey() {
        return publicKey;
    }

    public static String getMobSMSId() {
        return mobSMSId;
    }

    public static String getMobSMSKey() {
        return mobSMSKey;
    }
}
