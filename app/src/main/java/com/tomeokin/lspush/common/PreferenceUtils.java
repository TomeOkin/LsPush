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
package com.tomeokin.lspush.common;

import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.util.Base64;

import com.facebook.crypto.Crypto;
import com.facebook.crypto.Entity;
import com.facebook.crypto.exception.CryptoInitializationException;
import com.facebook.crypto.exception.KeyChainException;
import com.google.gson.Gson;
import com.tomeokin.lspush.data.crypt.CommonCrypto;

import java.io.IOException;
import java.lang.reflect.Type;

import timber.log.Timber;

public class PreferenceUtils {
    private final Gson mGson;
    private final SharedPreferences mPreference;
    private final com.facebook.crypto.Crypto mCrypto;

    public PreferenceUtils(Gson gson, SharedPreferences preferences, Crypto crypto) {
        mGson = gson;
        mPreference = preferences;
        mCrypto = crypto;
    }

    public <T> boolean put(@NonNull String key, T value) {
        String hashKey = CommonCrypto.hashPrefKey(key);
        if (value == null) {
            return mPreference.edit().remove(hashKey).commit();
        }

        try {
            put(key, hashKey, mGson.toJson(value));
        } catch (Exception e) {
            Timber.w(e, "put key");
            return false;
        }
        return true;
    }

    public <T> T get(@NonNull String key, Type type) {
        T value = null;
        try {
            String hashKey = CommonCrypto.hashPrefKey(key);
            String old = mPreference.getString(key, null);
            if (old != null) {
                Entity entity = Entity.create(key);
                Timber.i("get %s entity %s", key, entity.toString());
                byte[] data = mCrypto.decrypt(Base64.decode(old, Base64.NO_WRAP), entity);
                value = mGson.fromJson(new String(data, CharsetsSupport.UTF_8), type);
            }
        } catch (Exception e) {
            Timber.w(e);
            return null;
        }
        return value;
    }

    public boolean clearAll() {
        return mPreference.edit().clear().commit();
    }

    private void put(String key, String hashKey, String value)
        throws KeyChainException, CryptoInitializationException, IOException {
        Timber.i("put %s value %s", key, value);
        Entity entity = Entity.create(key); // original key
        byte[] data = mCrypto.encrypt(value.getBytes(CharsetsSupport.UTF_8), entity);

        // test
        Entity one = Entity.create(key);
        byte[] oneData = mCrypto.decrypt(data, one);
        Timber.i("get %s value %s", key, new String(oneData, CharsetsSupport.UTF_8));

        mPreference.edit().putString(key, Base64.encodeToString(data, Base64.NO_WRAP)).apply();
    }
}
