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
import android.content.ContextWrapper;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;

import com.alibaba.wireless.security.jaq.JAQException;
import com.alibaba.wireless.security.jaq.SecurityCipher;
import com.tomeokin.lspush.biz.common.UserScene;
import com.tomeokin.lspush.common.CharsetsSupport;
import com.tomeokin.lspush.data.crypt.BeeCrypto;
import com.tomeokin.lspush.data.crypt.CommonCrypto;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Properties;

import timber.log.Timber;

public final class LsPushConfig {
    private static final String LSPUSH_OK = "lspush.ok";
    private static final String SERVER_URL = "LSPUSH_SERVER_URL";
    private static final String PUBLIC_KEY = "LSPUSH_PUBLIC_KEY";
    private static final String MOB_SMS_ID = "MOB_SMS_ID";
    private static final String MOB_SMS_KEY = "MOB_SMS_KEY";
    private static final String JAQ_KEY = "JAQ_KEY";

    private static LsPushConfig sInstance;

    private static String serverUrl;
    private static String publicKey;
    private static String mobSMSId;
    private static String mobSMSKey;
    private static String jaqKey;

    public static void init(Context context) {
        if (sInstance == null) {
            sInstance = new LsPushConfig(context.getApplicationContext());
        }
    }

    private LsPushConfig(Context context) {
        try {
            loadMetadata(context);
            //loadConfig(context);
            //write(context);
            //read(context);
        } catch (Exception e) {
            Timber.tag(UserScene.TAG_APP).w("load metadata failure");
        }
    }

    public static LsPushConfig get() {
        return sInstance;
    }

    /**
     * load when init
     */
    private void loadMetadata(Context context) throws PackageManager.NameNotFoundException, IOException, JAQException {
        PackageManager packageManager = context.getPackageManager();
        ApplicationInfo appInfo =
            packageManager.getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
        if (appInfo != null && appInfo.metaData != null) {
            final Bundle metaData = appInfo.metaData;
            serverUrl = metaData.getString(SERVER_URL);
            jaqKey = metaData.getString(JAQ_KEY);
        }
    }

    public void loadProperty(Context context) {
        try {
            loadPropertyData(context);
        } catch (Exception e) {
            Timber.tag(UserScene.TAG_APP).w("load property failure");
        }
    }

    /**
     * load after BeeCrypto init
     */
    public void loadPropertyData(Context context) throws IOException, JAQException {
        Properties properties = new Properties();
        InputStream in = context.getAssets().open(LSPUSH_OK, Context.MODE_PRIVATE);
        BufferedReader reader = new BufferedReader(new InputStreamReader(in, CharsetsSupport.UTF_8));
        properties.load(reader);
        publicKey = BeeCrypto.get().decrypt(properties.getProperty(CommonCrypto.hashPrefKey(PUBLIC_KEY)));
        mobSMSId = BeeCrypto.get().decrypt(properties.getProperty(CommonCrypto.hashPrefKey(MOB_SMS_ID)));
        mobSMSKey = BeeCrypto.get().decrypt(properties.getProperty(CommonCrypto.hashPrefKey(MOB_SMS_KEY)));
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

    public static String getJaqKey() {
        return jaqKey;
    }

    /**
     * The follow method only use to create the {@link LsPushConfig#LSPUSH_OK} file. when you use them, modify
     * AndroidManifest.xml, app/build.gradle, buildsystem/config.gradle to uncomment some code. Also for {@link
     * LsPushConfig#init(Context)}
     */
    //region: build tools
    private File getFile(Context context, String path, String filename) {
        ContextWrapper contextWrapper = new ContextWrapper(context);
        File dir = contextWrapper.getDir(path, Context.MODE_PRIVATE);
        return new File(dir, filename);
    }

    private void write(Context context) throws JAQException, IOException {
        Properties properties = new Properties();
        SecurityCipher cipher = new SecurityCipher(context);
        properties.setProperty(CommonCrypto.hashPrefKey(PUBLIC_KEY), cipher.encryptString(publicKey, jaqKey));
        properties.setProperty(CommonCrypto.hashPrefKey(MOB_SMS_ID), cipher.encryptString(mobSMSId, jaqKey));
        properties.setProperty(CommonCrypto.hashPrefKey(MOB_SMS_KEY), cipher.encryptString(mobSMSKey, jaqKey));
        File file = getFile(context, "secure", LSPUSH_OK);
        OutputStream out = new FileOutputStream(file);
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out, CharsetsSupport.UTF_8));
        properties.store(writer, "lspush.ok version 3");
        out.close();
        writer.close();
    }

    private void read(Context context) throws IOException, JAQException {
        Properties properties = new Properties();
        SecurityCipher cipher = new SecurityCipher(context);
        File file = getFile(context, "secure", LSPUSH_OK);
        InputStream in = new FileInputStream(file);
        BufferedReader reader = new BufferedReader(new InputStreamReader(in, CharsetsSupport.UTF_8));
        properties.load(reader);
        Timber.d("key: %s, value: %s", PUBLIC_KEY,
            cipher.decryptString(properties.getProperty(CommonCrypto.hashPrefKey(PUBLIC_KEY)), jaqKey));
        Timber.d("key: %s, value: %s", MOB_SMS_ID,
            cipher.decryptString(properties.getProperty(CommonCrypto.hashPrefKey(MOB_SMS_ID)), jaqKey));
        Timber.d("key: %s, value: %s", MOB_SMS_KEY,
            cipher.decryptString(properties.getProperty(CommonCrypto.hashPrefKey(MOB_SMS_KEY)), jaqKey));
    }

    private void loadConfig(Context context) throws PackageManager.NameNotFoundException {
        PackageManager packageManager = context.getPackageManager();
        ApplicationInfo appInfo =
            packageManager.getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
        if (appInfo != null && appInfo.metaData != null) {
            final Bundle metaData = appInfo.metaData;
            serverUrl = metaData.getString(SERVER_URL);
            jaqKey = metaData.getString(JAQ_KEY);
            publicKey = metaData.getString(PUBLIC_KEY);
            mobSMSId = metaData.getString(MOB_SMS_ID);
            mobSMSKey = metaData.getString(MOB_SMS_KEY);
        }
    }
    //endregion
}
