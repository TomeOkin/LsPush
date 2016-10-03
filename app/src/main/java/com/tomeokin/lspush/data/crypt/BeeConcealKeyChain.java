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

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Base64;

import com.alibaba.wireless.security.jaq.JAQException;
import com.facebook.android.crypto.keychain.FixedSecureRandom;
import com.facebook.crypto.CryptoConfig;
import com.facebook.crypto.exception.KeyChainException;
import com.facebook.crypto.keychain.KeyChain;
import com.tomeokin.lspush.common.CharsetsSupport;

import java.util.Arrays;

import timber.log.Timber;

public class BeeConcealKeyChain implements KeyChain {
    private static final String LSPUSH_BEE = "lspush.bee";
    private static final String CIPHER_KEY = "CIPHER_KEY";
    private static final String MAC_KEY = "MAC_KEY";

    private final CryptoConfig mCryptoConfig;
    private final SharedPreferences mSharedPreferences;
    private final FixedSecureRandom mSecureRandom;
    protected byte[] mCipherKey = null;
    protected byte[] mMacKey;

    public BeeConcealKeyChain(Context context) {
        mSharedPreferences = context.getSharedPreferences(LSPUSH_BEE, Context.MODE_PRIVATE);
        mSecureRandom = new FixedSecureRandom();
        mCryptoConfig = CryptoConfig.KEY_256;
    }

    private byte[] maybeGenerateKey(String key, int length) throws KeyChainException, JAQException {
        byte[] data;
        String salt = mSharedPreferences.getString(CommonCrypto.hashPrefKey(key), "");
        if (!TextUtils.isEmpty(salt)) {
            try {
                data = BeeCrypto.get().decrypt(salt.getBytes(CharsetsSupport.UTF_8));
                data = Base64.decode(data, Base64.NO_WRAP);
                Timber.i("get key fom preferences, key.length: %d", data.length);
            } catch (Exception e) {
                Timber.w(e, "get key from preferences failure");
                data = generateKeyAndSave(key, length);
            }
        } else {
            data = generateKeyAndSave(key, length);
        }

        Timber.i("key length: %d", data.length);
        return data;
    }

    @SuppressLint("CommitPrefEdits")
    private byte[] generateKeyAndSave(String key, int length) throws JAQException {
        byte[] random = new byte[length];
        mSecureRandom.nextBytes(random);
        String data = BeeCrypto.get().encrypt(Base64.encodeToString(random, Base64.NO_WRAP));

        // test
        byte[] one = BeeCrypto.get().decrypt(data.getBytes(CharsetsSupport.UTF_8));
        byte[] raw = Base64.decode(one, Base64.NO_WRAP);
        Timber.i("key length of decrypt-one: %d", raw.length);

        mSharedPreferences.edit().putString(CommonCrypto.hashPrefKey(key), data).commit();
        return random;
    }

    @Override
    public byte[] getCipherKey() throws KeyChainException {
        if (mCipherKey == null) {
            try {
                mCipherKey = maybeGenerateKey(CIPHER_KEY, mCryptoConfig.keyLength);
            } catch (JAQException e) {
                Timber.w(e, "generate cipher key failure");
                throw new KeyChainException(e.getMessage(), e);
            }
        }
        Timber.i("key length: %d", mCipherKey.length);
        return mCipherKey;
    }

    @Override
    public byte[] getMacKey() throws KeyChainException {
        if (mMacKey == null) {
            try {
                mMacKey = maybeGenerateKey(MAC_KEY, 64);
            } catch (JAQException e) {
                throw new KeyChainException(e.getMessage(), e);
            }
        }
        return mMacKey;
    }

    @Override
    public byte[] getNewIV() throws KeyChainException {
        byte[] iv = new byte[mCryptoConfig.ivLength];
        mSecureRandom.nextBytes(iv);
        return iv;
    }

    @SuppressLint("CommitPrefEdits")
    @Override
    public void destroyKeys() {
        if (mCipherKey != null) {
            Arrays.fill(mCipherKey, (byte) 0);
        }
        if (mMacKey != null) {
            Arrays.fill(mMacKey, (byte) 0);
        }
        mCipherKey = null;
        mMacKey = null;
        mSharedPreferences.edit().remove(CommonCrypto.hashPrefKey(CIPHER_KEY)).remove(MAC_KEY).commit();
    }
}
