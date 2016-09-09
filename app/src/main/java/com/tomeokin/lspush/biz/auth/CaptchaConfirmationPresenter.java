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
package com.tomeokin.lspush.biz.auth;

import android.content.Context;
import android.content.res.Resources;
import android.support.annotation.Nullable;

import com.google.gson.Gson;
import com.tomeokin.lspush.R;
import com.tomeokin.lspush.biz.base.BaseActionCallback;
import com.tomeokin.lspush.biz.base.BasePresenter;
import com.tomeokin.lspush.biz.base.CommonCallback;
import com.tomeokin.lspush.biz.common.UserScene;
import com.tomeokin.lspush.common.SMSCaptchaUtils;
import com.tomeokin.lspush.data.crypt.Crypto;
import com.tomeokin.lspush.data.model.BaseResponse;
import com.tomeokin.lspush.data.model.CaptchaRequest;
import com.tomeokin.lspush.data.model.CryptoToken;
import com.tomeokin.lspush.data.model.RegisterData;
import com.tomeokin.lspush.data.remote.LsPushService;
import com.tomeokin.lspush.injection.qualifier.ActivityContext;
import com.tomeokin.lspush.injection.scope.PerActivity;

import javax.inject.Inject;

import retrofit2.Call;
import timber.log.Timber;

@PerActivity
public class CaptchaConfirmationPresenter extends BasePresenter<CaptchaConfirmationView> implements BaseActionCallback {
    private final LsPushService mLsPushService;
    private final Resources mResource;
    private final Gson mGson;
    private int mSendCaptchaActionId;
    private int mCheckCaptchaActionId;

    @Inject
    public CaptchaConfirmationPresenter(LsPushService lsPushService, @ActivityContext Context context, Gson gson) {
        mLsPushService = lsPushService;
        mResource = context.getResources();
        mGson = gson;
    }

    public void sendCaptcha(int actionId, CaptchaRequest request, String countryCode) {
        mSendCaptchaActionId = actionId;
        if (request.getSendObject().contains("@")) {
            Call<BaseResponse> call = mLsPushService.sendCaptcha(request);
            call.enqueue(new CommonCallback<>(mResource, actionId, getMvpView()));
        } else {
            SMSCaptchaUtils.sendCaptcha(countryCode, request.getSendObject());
        }
    }

    public void checkCaptcha(int actionId, CaptchaRequest request, String authCode) {
        mCheckCaptchaActionId = actionId;
        RegisterData registerData = new RegisterData();
        registerData.setCaptchaRequest(request);
        registerData.setAuthCode(authCode);
        String data = mGson.toJson(registerData, RegisterData.class);
        CryptoToken cryptoToken;
        try {
            cryptoToken = Crypto.encrypt(data);
        } catch (Exception e) {
            Timber.w(e);
            getMvpView().onActionFailure(actionId, null, mResource.getString(R.string.unexpected_error));
            return;
        }
        Call<BaseResponse> call = mLsPushService.checkCaptcha(cryptoToken);
        call.enqueue(new CommonCallback<>(mResource, actionId, getMvpView()));
    }

    @Override
    public void onActionFailure(int action, @Nullable BaseResponse response, String message) {
        if (action == SMSCaptchaUtils.SEND_CAPTCHA) {
            Timber.tag(UserScene.SEND_CAPTCHA).w(mResource.getString(action));
            getMvpView().onActionFailure(mSendCaptchaActionId, response,
                mResource.getString(R.string.send_captcha_error));
        } else if (action == SMSCaptchaUtils.CHECK_CAPTCHA) {
            Timber.tag(UserScene.CHECK_CAPTCHA).w(mResource.getString(action));
            getMvpView().onActionFailure(mCheckCaptchaActionId, response,
                mResource.getString(R.string.check_captcha_error));
        }
    }

    @Override
    public void onActionSuccess(int action, @Nullable BaseResponse response) {
        if (action == SMSCaptchaUtils.SEND_CAPTCHA) {
            getMvpView().onActionSuccess(mSendCaptchaActionId, response);
        } else if (action == SMSCaptchaUtils.CHECK_CAPTCHA) {
            getMvpView().onActionSuccess(mCheckCaptchaActionId, response);
        }
    }
}
