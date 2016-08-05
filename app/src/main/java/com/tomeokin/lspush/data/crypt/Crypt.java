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

import com.tomeokin.lspush.data.model.AccountAuth;

import java.nio.charset.Charset;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

public class Crypt {
    private static Crypt sCrypt;
    private PublicKey mPubKey;

    public static Crypt getInstance(final String pubKey)
            throws InvalidKeySpecException, NoSuchAlgorithmException {
        if (sCrypt == null) {
            sCrypt = new Crypt(pubKey);
        }
        return sCrypt;
    }

    /**
     * @param pubKey has encoding by Base64
     * @throws InvalidKeySpecException
     * @throws NoSuchAlgorithmException
     */
    private Crypt(final String pubKey) throws InvalidKeySpecException, NoSuchAlgorithmException {
        initPublicKey(pubKey);
    }

    private void initPublicKey(final String pubKey)
            throws NoSuchAlgorithmException, InvalidKeySpecException {
        final KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        final X509EncodedKeySpec pubKeySpec =
                new X509EncodedKeySpec(Base64.decode(pubKey, Base64.DEFAULT));
        this.mPubKey = keyFactory.generatePublic(pubKeySpec);
    }

    public AccountAuth encrypt(final String data)
            throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException,
            IllegalBlockSizeException, BadPaddingException {
        // seeing http://docs.oracle.com/javase/7/docs/api/javax/crypto/Cipher.html
        KeyGenerator keygen = KeyGenerator.getInstance("AES/CBC/PKCS5Padding");
        SecureRandom random = new SecureRandom();
        keygen.init(random);
        SecretKey key = keygen.generateKey();
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.WRAP_MODE, mPubKey);
        byte[] wrappedKey = cipher.wrap(key); // wrap RSA public key

        cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, key);
        byte[] encrypt = cipher.doFinal(data.getBytes(Charset.defaultCharset()));

        AccountAuth accountAuth = new AccountAuth();
        accountAuth.key = Base64.encodeToString(wrappedKey, Base64.DEFAULT);
        accountAuth.value = Base64.encodeToString(encrypt, Base64.DEFAULT);
        return accountAuth;
    }
}
