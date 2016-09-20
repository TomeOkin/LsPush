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
import android.os.Message;
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

    public static SMSCaptchaUtils init(Context context, String smsId, String smsKey) {
        if (sInstance == null) {
            sInstance = new SMSCaptchaUtils(context, smsId, smsKey);
        }
        return sInstance;
    }

    public void registerEventHandler(EventHandler eventHandler) {
        SMSSDK.registerEventHandler(eventHandler);
    }

    public void unregisterEventHandler(EventHandler eventHandler) {
        SMSSDK.unregisterEventHandler(eventHandler);
    }

    public void getSupportCountry() {
        SMSSDK.getSupportedCountries();
    }

    public void sendCaptcha(String country, String phone) {
        SMSSDK.getVerificationCode(country, phone);
    }

    public void submitCaptcha(String countryCode, String phone, String captcha) {
        SMSSDK.submitVerificationCode(countryCode, phone, captcha);
    }

    // see http://wiki.mob.com/android-%E7%9F%AD%E4%BF%A1sdk%E6%93%8D%E4%BD%9C%E5%9B%9E%E8%B0%83/
    // 由于 EventHandler 的回调有可能不是在 UI 线程，因此需要进行包装。
    public static final class SMSHandler extends Handler {
        private final BaseActionCallback mCallback;

        public SMSHandler(BaseActionCallback callback) {
            mCallback = callback;
        }

        @SuppressWarnings("unchecked")
        @Override
        public void dispatchMessage(Message msg) {
            if (msg.arg1 != SMSSDK.RESULT_COMPLETE) {
                Timber.w((Throwable) msg.obj);
                mCallback.onActionFailure(msg.what, null, null);
                return;
            }

            if (msg.what == GET_SUPPORTED_COUNTRIES) {
                final HashMap<String, String> countryList =
                    getCountryList((ArrayList<HashMap<String, Object>>) msg.obj);
                mCallback.onActionSuccess(msg.what, new SMSCountryListResponse(countryList));
            } else if (msg.what == SEND_CAPTCHA) {
                mCallback.onActionSuccess(msg.what, new SMSSentCaptchaResponse((Boolean) msg.obj));
            } else if (msg.what == CHECK_CAPTCHA) {
                final HashMap<String, Object> map = (HashMap<String, Object>) msg.obj;
                final String phone = (String) map.get("phone");
                final String countryCode = (String) map.get("country");
                mCallback.onActionSuccess(msg.what, new SMSCheckCaptchaResponse(phone, countryCode));
            }
        }

        public void removeAllMessage() {
            removeMessages(GET_SUPPORTED_COUNTRIES);
            removeMessages(SEND_CAPTCHA);
            removeMessages(CHECK_CAPTCHA);
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

    public static final class CustomEventHandler extends EventHandler {
        private final SMSHandler mHandler;

        public CustomEventHandler(SMSHandler handler) {
            mHandler = handler;
        }

        @SuppressWarnings("unchecked")
        @Override
        public void afterEvent(final int event, final int result, final Object data) {
            mHandler.obtainMessage(event, result, 0, data).sendToTarget();
        }
    }
}
