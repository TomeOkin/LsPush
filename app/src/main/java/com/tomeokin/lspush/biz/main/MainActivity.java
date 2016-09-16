package com.tomeokin.lspush.biz.main;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.alibaba.wireless.security.jaq.SecurityCipher;
import com.tomeokin.lspush.R;
import com.tomeokin.lspush.biz.auth.SignOutActivity;
import com.tomeokin.lspush.config.LsPushConfig;

import java.util.Arrays;

import timber.log.Timber;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try {
            SecurityCipher cipher = new SecurityCipher(this);
            //加密字串
            //"helloword":待加密字串
            //"ka1"		 :加密用的密钥key
            String encryptString = cipher.encryptString("helloword", LsPushConfig.getJaqKey());

            //解密字串
            //encryptString:待解密字串
            //"ka1"       :解密用的密钥key
            String decryptString = cipher.decryptString(encryptString, LsPushConfig.getJaqKey());
            Timber.i("decrypt 1: %s", decryptString);

            byte[] dataBytes = {1,2,3,4,5,6,7,8,9,0};

            //加密数组
            //dataBytes  :待加密数组
            //"ka1"		 :加密用的密钥key
            byte[] encryptBytes = cipher.encryptBinary(dataBytes, LsPushConfig.getJaqKey());

            //解密数组
            //encryptBytes:待解密数组
            //"ka1"		 :解密用的密钥key
            byte[] decryptBytes = cipher.decryptBinary(encryptBytes, LsPushConfig.getJaqKey());
            Timber.i("decrypt 2: %s", Arrays.toString(decryptBytes));
        } catch (Exception e) {
            Timber.w(e);
        }

        boolean hasLogin = false;
        if (!hasLogin) {
            Intent intent = new Intent(this, SignOutActivity.class);
            startActivity(intent);
            finish();
        }

        //Bundle bundle = CollectionTargetFragment.prepareArgument("http://www.jianshu.com/p/2a9fcf3c11e4");
        //Navigator.moveTo(this, CollectionTargetFragment.class, bundle);
    }
}
