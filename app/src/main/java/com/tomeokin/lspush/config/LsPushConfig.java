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

import com.alibaba.wireless.security.jaq.JAQException;
import com.tomeokin.lspush.biz.common.UserScene;
import com.tomeokin.lspush.common.CharsetsSupport;
import com.tomeokin.lspush.data.crypt.BeeCrypto;
import com.tomeokin.lspush.data.crypt.CommonCrypto;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;

import timber.log.Timber;

public final class LsPushConfig {
    // when you change those name, please change them with LsPushOkBuildTool
    private static final String LSPUSH_OK = "lspush.ok";
    private static final String PUBLIC_KEY = "LSPUSH_PUBLIC_KEY";
    private static final String MOB_SMS_ID = "MOB_SMS_ID";
    private static final String MOB_SMS_KEY = "MOB_SMS_KEY";

    private static LsPushConfig sInstance;

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
            loadPropertyData(context);
        } catch (Exception e) {
            Timber.tag(UserScene.TAG_APP).w("load property failure");
        }
    }

    public static LsPushConfig get() {
        return sInstance;
    }

    /**
     * load after BeeCrypto init, otherwise it will throw NullException
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
