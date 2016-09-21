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
package com.tomeokin.lspush.biz.usercase.auth;

import android.content.Context;
import android.support.annotation.Nullable;

import com.google.gson.Gson;
import com.tomeokin.lspush.R;
import com.tomeokin.lspush.biz.base.BaseAction;
import com.tomeokin.lspush.biz.base.BaseActionCallback;
import com.tomeokin.lspush.biz.base.CommonCallback;
import com.tomeokin.lspush.biz.common.NoGuarantee;
import com.tomeokin.lspush.biz.common.UserScene;
import com.tomeokin.lspush.common.SMSCaptchaUtils;
import com.tomeokin.lspush.data.crypt.Crypto;
import com.tomeokin.lspush.data.model.BaseResponse;
import com.tomeokin.lspush.data.model.CaptchaRequest;
import com.tomeokin.lspush.data.model.CryptoToken;
import com.tomeokin.lspush.data.model.RegisterData;
import com.tomeokin.lspush.data.remote.LsPushService;

import cn.smssdk.EventHandler;
import retrofit2.Call;
import timber.log.Timber;

public class CheckCaptchaAction extends BaseAction implements BaseActionCallback {
    private final LsPushService mLsPushService;
    private final Gson mGson;

    private SMSCaptchaUtils.SMSHandler mHandler;
    private EventHandler mEventHandler;
    private Call<BaseResponse> mCheckCaptchaCall;

    public CheckCaptchaAction(Context context, LsPushService lsPushService, Gson gson) {
        super(context.getResources());
        SMSCaptchaUtils.init(context);
        mLsPushService = lsPushService;
        mGson = gson;
    }

    public void checkCaptcha(CaptchaRequest request, String authCode, String countryCode) {
        if (request.getSendObject().contains("@")) {
            checkCaptcha(request, authCode);
        } else {
            SMSCaptchaUtils.submitCaptcha(countryCode, request.getSendObject(), authCode);
        }
    }

    private void checkCaptcha(CaptchaRequest request, String authCode) {
        RegisterData registerData = new RegisterData();
        registerData.setCaptchaRequest(request);
        registerData.setAuthCode(authCode);
        String data = mGson.toJson(registerData, RegisterData.class);
        CryptoToken cryptoToken;
        try {
            cryptoToken = Crypto.get().encrypt(data);
        } catch (Exception e) {
            Timber.w(e);
            mCallback.onActionFailure(UserScene.ACTION_CHECK_CAPTCHA, null,
                mResource.getString(R.string.unexpected_error));
            return;
        }

        checkAndCancel(mCheckCaptchaCall);
        mCheckCaptchaCall = mLsPushService.checkCaptcha(cryptoToken);
        mCheckCaptchaCall.enqueue(new CommonCallback<>(mResource, UserScene.ACTION_CHECK_CAPTCHA, mCallback));
    }

    @Override
    public void onActionFailure(int action, @Nullable BaseResponse response, String message) {
        if (action == SMSCaptchaUtils.CHECK_CAPTCHA && mCallback != null) {
            Timber.tag(UserScene.CHECK_CAPTCHA).w(mResource.getString(action));
            mCallback.onActionFailure(UserScene.ACTION_CHECK_CAPTCHA, response,
                mResource.getString(R.string.check_captcha_error));
        }
    }

    @Override
    public void onActionSuccess(int action, @Nullable BaseResponse response) {
        if (action == SMSCaptchaUtils.CHECK_CAPTCHA && mCallback != null) {
            mCallback.onActionSuccess(UserScene.ACTION_CHECK_CAPTCHA, response);
        }
    }

    @Override
    public void attach(BaseActionCallback callback) {
        super.attach(callback);
        mHandler = new SMSCaptchaUtils.SMSHandler(this);
        mEventHandler = new SMSCaptchaUtils.CustomEventHandler(mHandler);
        SMSCaptchaUtils.registerEventHandler(mEventHandler);
    }

    @Override
    public void detach() {
        super.detach();
        SMSCaptchaUtils.unregisterEventHandler(mEventHandler);
        mEventHandler = null;
        mHandler.removeAllMessage();
        mHandler = null;
        checkAndCancel(mCheckCaptchaCall);
        mCheckCaptchaCall = null;
    }

    @NoGuarantee
    @Override
    public void cancel(int action) {
        super.cancel(action);
        if (action == UserScene.ACTION_CHECK_CAPTCHA) {
            checkAndCancel(mCheckCaptchaCall);
        }
    }
}
