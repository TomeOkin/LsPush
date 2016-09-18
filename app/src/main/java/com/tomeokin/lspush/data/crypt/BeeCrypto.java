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

public class BeeCrypto {
    private static BeeCrypto beeCrypto;
    private static SecurityCipher cipher;
    private static String jaqKey;

    public static void init(Context context, final String jqaKey) {
        if (beeCrypto == null) {
            beeCrypto = new BeeCrypto(context.getApplicationContext(), jqaKey);
        }
    }

    private BeeCrypto(Context context, final String jaqKey) {
        BeeCrypto.jaqKey = jaqKey;
        cipher = new SecurityCipher(context);
    }

    public static byte[] encrypt(byte[] data) throws JAQException {
        return data == null || data.length == 0 ? data : cipher.encryptBinary(data, jaqKey);
    }

    public static String encrypt(String data) throws JAQException {
        return TextUtils.isEmpty(data) ? data : cipher.encryptString(data, jaqKey);
    }

    public static byte[] decrypt(byte[] data) throws JAQException {
        return data == null || data.length == 0 ? data : cipher.decryptBinary(data, jaqKey);
    }

    public static String decrypt(String data) throws JAQException {
        return TextUtils.isEmpty(data) ? data : cipher.decryptString(data, jaqKey);
    }
}
