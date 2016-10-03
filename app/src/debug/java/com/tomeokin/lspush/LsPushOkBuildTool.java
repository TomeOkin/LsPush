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
package com.tomeokin.lspush;

import android.content.Context;
import android.content.ContextWrapper;

import com.alibaba.wireless.security.jaq.JAQException;
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

public class LsPushOkBuildTool {
    private static final String LSPUSH_OK = "lspush.ok";
    private static final String PUBLIC_KEY = "LSPUSH_PUBLIC_KEY";
    private static final String MOB_SMS_ID = "MOB_SMS_ID";
    private static final String MOB_SMS_KEY = "MOB_SMS_KEY";

    private File getFile(Context context, String path, String filename) {
        ContextWrapper contextWrapper = new ContextWrapper(context);
        File dir = contextWrapper.getDir(path, Context.MODE_PRIVATE);
        return new File(dir, filename);
    }

    private void write(Context context) throws JAQException, IOException {
        Properties properties = new Properties();
        properties.setProperty(CommonCrypto.hashPrefKey(PUBLIC_KEY),
            BeeCrypto.get().encrypt(BuildConfig.LSPUSH_PUBLIC_KEY));
        properties.setProperty(CommonCrypto.hashPrefKey(MOB_SMS_ID),
            BeeCrypto.get().encrypt(BuildConfig.MOB_SMS_ID));
        properties.setProperty(CommonCrypto.hashPrefKey(MOB_SMS_KEY),
            BeeCrypto.get().encrypt(BuildConfig.MOB_SMS_KEY));
        File file = getFile(context, "secure", LSPUSH_OK);
        OutputStream out = new FileOutputStream(file);
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out, CharsetsSupport.UTF_8));
        properties.store(writer, "lspush.ok version 3");
        out.close();
        writer.close();
    }

    private void read(Context context) throws IOException, JAQException {
        Properties properties = new Properties();
        File file = getFile(context, "secure", LSPUSH_OK);
        InputStream in = new FileInputStream(file);
        BufferedReader reader = new BufferedReader(new InputStreamReader(in, CharsetsSupport.UTF_8));
        properties.load(reader);
        Timber.d("key: %s, value: %s", PUBLIC_KEY,
            BeeCrypto.get().decrypt(properties.getProperty(CommonCrypto.hashPrefKey(PUBLIC_KEY))));
        Timber.d("key: %s, value: %s", MOB_SMS_ID,
            BeeCrypto.get().decrypt(properties.getProperty(CommonCrypto.hashPrefKey(MOB_SMS_ID))));
        Timber.d("key: %s, value: %s", MOB_SMS_KEY,
            BeeCrypto.get().decrypt(properties.getProperty(CommonCrypto.hashPrefKey(MOB_SMS_KEY))));
    }
}
