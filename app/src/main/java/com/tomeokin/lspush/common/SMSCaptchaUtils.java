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
package com.tomeokin.lspush.common;

import android.content.Context;
import android.os.Handler;
import android.text.TextUtils;

import com.tomeokin.lspush.biz.base.BaseActionCallback;
import com.tomeokin.lspush.data.model.SMSCheckCaptchaResponse;
import com.tomeokin.lspush.data.model.SMSCountryListResponse;
import com.tomeokin.lspush.data.model.SMSSentCaptchaResponse;

import java.util.ArrayList;
import java.util.HashMap;

import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;
import timber.log.Timber;

public class SMSCaptchaUtils {
    public static final int GET_SUPPORTED_COUNTRIES = SMSSDK.EVENT_GET_SUPPORTED_COUNTRIES;
    public static final int SEND_CAPTCHA = SMSSDK.EVENT_GET_VERIFICATION_CODE;
    public static final int CHECK_CAPTCHA = SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE;
    public static final int SEND_VOICE_CAPTCHA = SMSSDK.EVENT_GET_VOICE_VERIFICATION_CODE;

    private static SMSCaptchaUtils sInstance;

    private SMSCaptchaUtils(Context context, String smsId, String smsKey) {
        SMSSDK.initSDK(context, smsId, smsKey);
    }

    public static void init(Context context, String smsId, String smsKey) {
        if (sInstance == null) {
            sInstance = new SMSCaptchaUtils(context, smsId, smsKey);
        }
    }

    public static void registerEventHandler(EventHandler eventHandler) {
        SMSSDK.registerEventHandler(eventHandler);
    }

    public static void unregisterEventHandler(EventHandler eventHandler) {
        SMSSDK.unregisterEventHandler(eventHandler);
    }

    public static void getSupportCountry() {
        SMSSDK.getSupportedCountries();
    }

    public static void sendCaptcha(String country, String phone) {
        SMSSDK.getVerificationCode(country, phone);
    }

    public static void submitCaptcha(String countryCode, String phone, String captcha) {
        SMSSDK.submitVerificationCode(countryCode, phone, captcha);
    }

    public static final class CustomEventHandler extends EventHandler {
        private final Handler mHandler;
        private final BaseActionCallback mCallback;

        public CustomEventHandler(Handler handler, BaseActionCallback callback) {
            mHandler = handler;
            mCallback = callback;
        }

        @SuppressWarnings("unchecked") @Override
        public void afterEvent(final int event, final int result, final Object data) {
            if (result != SMSSDK.RESULT_COMPLETE) {
                Timber.w((Throwable) data);
                mHandler.post(new Runnable() {
                    @Override public void run() {
                        mCallback.onActionFailure(event, null);
                    }
                });
                return;
            }

            if (event == GET_SUPPORTED_COUNTRIES) {
                final HashMap<String, String> countryList = getCountryList((ArrayList<HashMap<String, Object>>) data);
                mHandler.post(new Runnable() {
                    @Override public void run() {
                        mCallback.onActionSuccess(event, new SMSCountryListResponse(countryList));
                    }
                });
            } else if (event == SEND_CAPTCHA) {
                mHandler.post(new Runnable() {
                    @Override public void run() {
                        mCallback.onActionSuccess(event, new SMSSentCaptchaResponse((Boolean) data));
                    }
                });
            } else if (event == CHECK_CAPTCHA) {
                final HashMap<String, Object> map = (HashMap<String, Object>) data;
                final String phone = (String) map.get("phone");
                final String countryCode = (String) map.get("country");
                mHandler.post(new Runnable() {
                    @Override public void run() {
                        mCallback.onActionSuccess(event, new SMSCheckCaptchaResponse(phone, countryCode));
                    }
                });
            }
        }

        // 解析国家列表
        private HashMap<String, String> getCountryList(ArrayList<HashMap<String, Object>> countries) {
            // 国家号码规则
            HashMap<String, String> countryRules = new HashMap<>(countries.size());

            for (HashMap<String, Object> country : countries) {
                String code = (String) country.get("zone");
                String rule = (String) country.get("rule");
                if (TextUtils.isEmpty(code) || TextUtils.isEmpty(rule)) {
                    continue;
                }

                countryRules.put(code, rule);
            }
            return countryRules;
        }
    }
}
