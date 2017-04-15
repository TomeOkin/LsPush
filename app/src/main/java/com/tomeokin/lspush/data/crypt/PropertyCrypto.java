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
import com.google.gson.Gson;
import com.tomeokin.lspush.util.CharsetsSupport;

import java.lang.reflect.Type;

public class PropertyCrypto {
    private com.facebook.crypto.Crypto mFbCrypto;
    private Gson mGson;

    public PropertyCrypto(Context context, Gson gson) {
        BeeConcealKeyChain keyChain = new BeeConcealKeyChain(context);
        mFbCrypto = AndroidConceal.get().createCrypto256Bits(keyChain);
        mGson = gson;
    }

    public String decrypt(String key, String value, Type type) throws CryptoException {
        try {
            Entity entity = Entity.create(key);
            byte[] data = mFbCrypto.decrypt(Base64.decode(value, Base64.NO_WRAP), entity);
            return mGson.fromJson(new String(data, CharsetsSupport.UTF_8), type);
        } catch (Exception e) {
            throw new CryptoException(e);
        }
    }


}
