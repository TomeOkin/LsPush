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

import com.tomeokin.lspush.biz.common.UserScene;
import com.tomeokin.lspush.util.CharsetsSupport;
import com.tomeokin.lspush.config.LsPushConfig;
import com.tomeokin.lspush.data.model.CryptoToken;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
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
import javax.crypto.spec.IvParameterSpec;

import timber.log.Timber;

public class Crypto {
    private static Crypto cryptor;
    private static PublicKey pubKey;

    public static Crypto get() {
        if (cryptor == null) {
            try {
                cryptor = new Crypto(LsPushConfig.getPublicKey());
            } catch (Exception e) {
                Timber.tag(UserScene.TAG_APP).wtf(e, "generate cryptor instance failure");
            }
        }
        return cryptor;
    }

    /**
     * @param pubKey has encoding by Base64
     */
    private Crypto(final String pubKey)
        throws InvalidKeySpecException, NoSuchAlgorithmException, NoSuchProviderException {
        initKey(pubKey);
    }

    private void initKey(final String pubKey)
        throws NoSuchAlgorithmException, InvalidKeySpecException {
        final KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        final X509EncodedKeySpec pubKeySpec = new X509EncodedKeySpec(Base64.decode(pubKey, Base64.NO_WRAP));
        Crypto.pubKey = keyFactory.generatePublic(pubKeySpec);
    }

    public CryptoToken encrypt(String data)
        throws NoSuchPaddingException, InvalidKeyException, NoSuchAlgorithmException, IllegalBlockSizeException,
        BadPaddingException, NoSuchProviderException, InvalidAlgorithmParameterException {
        return encrypt(data.getBytes(CharsetsSupport.UTF_8));
    }

    /**
     * @see Cipher, there have a table of support transformations
     * @see <a href="http://docs.oracle.com/javase/7/docs/technotes/guides/security/SunProviders.html#cipherTable">javase-7-support
     * transformations</a>，可通过 Cipher.getInstance("RSA").getProvider().getClass().getName() 查看默认提供者。
     * @see <a href="https://nelenkov.blogspot.com/2012/04/using-password-based-encryption-on.html">Android
     * 下的最佳实践</>
     * @see <a href="http://grokbase.com/t/gg/android-developers/12cd9pdwer/cipher-wrap-not-working-in-android-4-2">Wrap
     * 方式下存在的问题及解决方案</a>
     * @see <a href="http://www.droidsec.cn/android%E5%BA%94%E7%94%A8%E5%AE%89%E5%85%A8%E5%BC%80%E5%8F%91%E4%B9%8B%E6%B5%85%E8%B0%88%E5%8A%A0%E5%AF%86%E7%AE%97%E6%B3%95%E7%9A%84%E5%9D%91/">Android应用安全开发之浅谈加密算法的坑</a>
     * it tell you some best way to use those algorithm.
     * @see <a href="http://techmedia-think.hatenablog.com/entry/20110527/1306499951">JavaとRubyで暗号化/復号化</a>, a good post
     * of AES.
     * @see <a href="http://qiita.com/f_nishio/items/485490dea126dbbb5001">a example of
     * RSA/ECB/OAEPWithSHA-256AndMGF1Padding</a>
     */
    public CryptoToken encrypt(byte[] data)
        throws NoSuchAlgorithmException, NoSuchProviderException, NoSuchPaddingException,
        InvalidAlgorithmParameterException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        // get a AES key for encrypt
        KeyGenerator keygen = KeyGenerator.getInstance("AES");
        // Android default key size is not support in standard java
        keygen.init(128);
        SecretKey secretKey = keygen.generateKey();

        // AES + ECB is not secure, suggest to use AES + CBC
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding", "BC");
        SecureRandom random = new SecureRandom();
        byte[] iv = new byte[cipher.getBlockSize()];
        random.nextBytes(iv);
        IvParameterSpec ivParams = new IvParameterSpec(iv);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivParams);
        byte[] encrypt = cipher.doFinal(data);

        // Android default RSA transformation is different from standard JCE
        // when using RSA/ECB/OAEPWithSHA-256AndMGF1Padding, it will throw ArrayOutOfBound exception.
        cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding", "BC");
        cipher.init(Cipher.WRAP_MODE, pubKey);
        byte[] wrappedKey = cipher.wrap(secretKey); // wrap RSA public key

        CryptoToken cryptoToken = new CryptoToken();
        cryptoToken.key = Base64.encodeToString(wrappedKey, Base64.NO_WRAP);
        cryptoToken.param = Base64.encodeToString(iv, Base64.NO_WRAP);
        cryptoToken.value = Base64.encodeToString(encrypt, Base64.NO_WRAP);
        return cryptoToken;
    }
}
