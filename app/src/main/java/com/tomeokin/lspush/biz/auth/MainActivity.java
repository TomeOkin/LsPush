package com.tomeokin.lspush.biz.auth;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import com.tomeokin.lspush.R;
import com.tomeokin.lspush.common.SMSCaptchaUtils;

import java.util.HashMap;

import cn.smssdk.EventHandler;
import timber.log.Timber;

public class MainActivity extends AppCompatActivity implements SMSCaptchaUtils.SMSCaptchaCallback {
    private SMSCaptchaUtils mSMSUtil;
    private EventHandler mEventHandler;
    private Handler mHandler;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = new Intent(this, SignOutActivity.class);
        startActivity(intent);
        //mSMSUtil = SMSCaptchaUtils.getInstance(this);
        //mHandler = new Handler();
        //mEventHandler = new SMSCaptchaUtils.CustomEventHandler(mHandler, this);
        //mSMSUtil.registerEventHandler(mEventHandler);
        //mSMSUtil.getSupportCountry();
        //mSMSUtil.sendCaptcha("86", "your_phone"); // 使用 +86 会验证失败
        //mSMSUtil.submitCaptcha("86", "your_phone", "9107");
    }

    @Override public void onSMSCaptchaError(@SMSCaptchaUtils.ErrorType int event) {

    }

    @Override public void onReceivedCountryList(HashMap<String, String> countryList) {
        //Timber.i("onReceivedCountryList");
        //for (String key : countryList.keySet()) {
        //    Log.i("country", "country-key: " + key);
        //    Log.i("country", "country-value: " + countryList.get(key));
        //}
    }

    @Override public void onSentCaptchaSuccess(boolean autoReadCaptcha) {
        Timber.i("autoReadCaptcha: %s", autoReadCaptcha);
    }

    @Override public void onSubmitCaptchaSuccess(String phone, String countryCode) {
        // 返回校验的手机和国家代码
        //for (String key : phone.keySet()) {
        //    Timber.i("key: %s", key);
        //    Timber.i("value: %s", phone.get(key));
        //}
        // phone your_phone
        // country 86
    }

    @Override protected void onDestroy() {
        super.onDestroy();
        //mSMSUtil.unregisterEventHandler(mEventHandler);
        mEventHandler = null;
        mHandler = null;
    }
}
