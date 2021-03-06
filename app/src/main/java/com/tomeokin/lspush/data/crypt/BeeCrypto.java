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
import android.text.TextUtils;

import com.alibaba.wireless.security.jaq.JAQException;
import com.alibaba.wireless.security.jaq.SecurityCipher;
import com.alibaba.wireless.security.jaq.SecurityInit;
import com.tomeokin.lspush.BuildConfig;

import timber.log.Timber;

public class BeeCrypto {
    private static BeeCrypto beeCrypto;
    private final SecurityCipher mCipher;
    private static final String mJaqKey;

    static {
        mJaqKey = BuildConfig.JAQ_KEY;
    }

    public static void init(Context context) {
        if (beeCrypto == null) {
            beeCrypto = new BeeCrypto(context.getApplicationContext());
        }
    }

    public static BeeCrypto get() {
        return beeCrypto;
    }

    private BeeCrypto(Context context) {
        try {
            SecurityInit.Initialize(context);
        } catch (JAQException e) {
            Timber.wtf("init jaq failure, errorCode = %d", e.getErrorCode());
        }
        mCipher = new SecurityCipher(context);
    }

    public byte[] encrypt(byte[] data) throws JAQException {
        return data == null || data.length == 0 ? data : mCipher.encryptBinary(data, mJaqKey);
    }

    public String encrypt(String data) throws JAQException {
        return TextUtils.isEmpty(data) ? data : mCipher.encryptString(data, mJaqKey);
    }

    public byte[] decrypt(byte[] data) throws JAQException {
        return data == null || data.length == 0 ? data : mCipher.decryptBinary(data, mJaqKey);
    }

    public String decrypt(String data) throws JAQException {
        return TextUtils.isEmpty(data) ? data : mCipher.decryptString(data, mJaqKey);
    }
}
