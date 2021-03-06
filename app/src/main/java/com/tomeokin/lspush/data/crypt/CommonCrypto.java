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

import android.util.Base64;

import com.tomeokin.lspush.util.CharsetsSupport;

import java.security.MessageDigest;

import timber.log.Timber;

public class CommonCrypto {
    /**
     * key name hash function
     * https://github.com/scottyab/secure-preferences/blob/master/library/src/main/java/com/securepreferences/SecurePreferences.java#L247
     */
    public static String hashPrefKey(String prefKey) {
        try {
            final MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] bytes = prefKey.getBytes(CharsetsSupport.UTF_8);
            digest.update(bytes, 0, bytes.length);
            return Base64.encodeToString(digest.digest(), Base64.NO_WRAP);
        } catch (Exception e) {
            Timber.w(e, "hash key-name failure");
        }
        return prefKey;
    }
}
