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
package com.tomeokin.lspush.data.crypt;

import android.content.Context;
import android.util.Base64;

import com.facebook.android.crypto.keychain.AndroidConceal;
import com.facebook.crypto.Entity;
import com.orhanobut.hawk.NoEncryption;

public class BeeEncryption extends NoEncryption {
    private final com.facebook.crypto.Crypto crypto;

    public BeeEncryption(Context context) {
        BeeConcealKeyChain keyChain = new BeeConcealKeyChain(context);
        crypto = AndroidConceal.get().createCrypto256Bits(keyChain);
    }

    @Override
    public boolean init() {
        return crypto.isAvailable();
    }

    @Override
    public String encrypt(String key, String value) throws Exception {
        Entity entity = Entity.create(key);
        byte[] bytes = crypto.encrypt(value.getBytes(), entity);
        return Base64.encodeToString(bytes, Base64.NO_WRAP);
    }

    @Override
    public String decrypt(String key, String value) throws Exception {
        Entity entity = Entity.create(key);
        byte[] decodedBytes = Base64.decode(value, Base64.NO_WRAP);
        byte[] bytes = crypto.decrypt(decodedBytes, entity);
        return new String(bytes);
    }
}
