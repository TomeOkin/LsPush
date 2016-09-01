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

import com.google.gson.Gson;
import com.tomeokin.lspush.R;
import com.tomeokin.lspush.biz.base.BasePresenter;
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

import java.io.IOException;
import java.util.HashMap;

import javax.inject.Inject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

@PerActivity
public class CaptchaConfirmationPresenter extends BasePresenter<CaptchaConfirmationView>
    implements SMSCaptchaUtils.SMSCaptchaCallback {
    private final LsPushService mLsPushService;
    private final Resources mResource;
    private final Gson mGson;

    @Inject
    public CaptchaConfirmationPresenter(LsPushService lsPushService, @ActivityContext Context context, Gson gson) {
        mLsPushService = lsPushService;
        mResource = context.getResources();
        mGson = gson;
    }

    public void sendCaptcha(CaptchaRequest request, String countryCode) {
        if (request.getSendObject().contains("@")) {
            sendEmailCaptcha(request);
        } else {
            SMSCaptchaUtils.sendCaptcha(countryCode, request.getSendObject());
        }
    }

    private void sendEmailCaptcha(CaptchaRequest request) {
        Call<BaseResponse> call = mLsPushService.sendCaptcha(request);
        call.enqueue(new Callback<BaseResponse>() {
            @Override public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
                if (response.isSuccessful()) {
                    BaseResponse baseResponse = response.body();
                    if (baseResponse.getResultCode() == BaseResponse.COMMON_SUCCESS) {
                        getMvpView().onSentCaptchaCodeSuccess();
                    } else {
                        getMvpView().onSentCaptchaCodeFailure(baseResponse.getResult());
                    }
                } else {
                    try {
                        Timber.tag(UserScene.SEND_CAPTCHA).w(response.errorBody().string());
                        getMvpView().onSentCaptchaCodeFailure(mResource.getString(R.string.send_captcha_error));
                    } catch (IOException e) {
                        Timber.w(e);
                    }
                }
            }

            @Override public void onFailure(Call<BaseResponse> call, Throwable t) {
                Timber.w(t);
                getMvpView().onSentCaptchaCodeFailure(mResource.getString(R.string.unexpected_error));
            }
        });
    }

    public void checkCaptcha(CaptchaRequest request, String authCode) {
        RegisterData registerData = new RegisterData();
        registerData.setCaptchaRequest(request);
        registerData.setAuthCode(authCode);
        String data = mGson.toJson(registerData, RegisterData.class);
        CryptoToken cryptoToken;
        try {
            cryptoToken = Crypto.encrypt(data);
        } catch (Exception e) {
            Timber.w(e);
            getMvpView().onCheckCaptchaFailure(mResource.getString(R.string.unexpected_error));
            return;
        }
        Call<BaseResponse> call = mLsPushService.checkCaptcha(cryptoToken);
        call.enqueue(new Callback<BaseResponse>() {
            @Override public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
                if (response.isSuccessful()) {
                    BaseResponse baseResponse = response.body();
                    if (baseResponse.getResultCode() == BaseResponse.COMMON_SUCCESS) {
                        getMvpView().onCheckCaptchaSuccess();
                    } else {
                        getMvpView().onCheckCaptchaFailure(baseResponse.getResult());
                    }
                } else {
                    try {
                        Timber.tag(UserScene.CHECK_CAPTCHA).w(response.errorBody().string());
                        getMvpView().onCheckCaptchaFailure(mResource.getString(R.string.check_captcha_error));
                    } catch (IOException e) {
                        Timber.w(e);
                    }
                }
            }

            @Override public void onFailure(Call<BaseResponse> call, Throwable t) {
                Timber.w(t);
                getMvpView().onCheckCaptchaFailure(mResource.getString(R.string.unexpected_error));
            }
        });
    }

    @Override public void onReceivedCountryList(HashMap<String, String> countryList) {

    }

    @Override public void onSMSCaptchaError(@SMSCaptchaUtils.ErrorType int event) {
        if (event == SMSCaptchaUtils.SEND_CAPTCHA) {
            Timber.tag(UserScene.SEND_CAPTCHA).w(mResource.getString(event));
            getMvpView().onSentCaptchaCodeFailure(mResource.getString(R.string.send_captcha_error));
        } else if (event == SMSCaptchaUtils.CHECK_CAPTCHA) {
            Timber.tag(UserScene.CHECK_CAPTCHA).w(mResource.getString(event));
            getMvpView().onCheckCaptchaFailure(mResource.getString(R.string.check_captcha_error));
        }
    }

    @Override public void onSentCaptchaSuccess(boolean autoReadCaptcha) {
        getMvpView().onSentCaptchaCodeSuccess();
    }

    @Override public void onSubmitCaptchaSuccess(String phone, String countryCode) {
        getMvpView().onCheckCaptchaSuccess();
    }
}
