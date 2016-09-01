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

import com.tomeokin.lspush.R;
import com.tomeokin.lspush.biz.base.BasePresenter;
import com.tomeokin.lspush.biz.common.UserScene;
import com.tomeokin.lspush.common.SMSCaptchaUtils;
import com.tomeokin.lspush.data.model.BaseResponse;
import com.tomeokin.lspush.data.model.CaptchaRequest;
import com.tomeokin.lspush.data.remote.LsPushService;
import com.tomeokin.lspush.injection.qualifier.ActivityContext;
import com.tomeokin.lspush.injection.scope.PerActivity;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

@PerActivity
public class CaptchaPresenter extends BasePresenter<CaptchaView> implements SMSCaptchaUtils.SMSCaptchaCallback {
    private final LsPushService mLsPushService;
    private final Resources mResource;

    @Inject public CaptchaPresenter(LsPushService lsPushService, @ActivityContext Context context) {
        mLsPushService = lsPushService;
        mResource = context.getResources();
    }

    public List<String> getHistoryUserEmails() {
        return null;
    }

    public void sendCaptchaCode(CaptchaRequest request, String countryCode) {
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
                    BaseResponse accessResponse = response.body();
                    if (accessResponse.getResultCode() == BaseResponse.COMMON_SUCCESS) {
                        getMvpView().moveToCaptchaVerify();
                    } else {
                        getMvpView().onSentCaptchaCodeFailure(accessResponse.getResult());
                    }
                } else {
                    try {
                        Timber.tag("network").w(response.errorBody().string());
                        getMvpView().onSentCaptchaCodeFailure(response.errorBody().string());
                    } catch (IOException e) {
                        Timber.w(e);
                    }
                }
            }

            @Override public void onFailure(Call<BaseResponse> call, Throwable t) {
                getMvpView().onSentCaptchaCodeFailure(mResource.getString(R.string.unexpected_error));
                Timber.w(t);
            }
        });
    }

    @Override public void onReceivedCountryList(HashMap<String, String> countryList) {

    }

    @Override public void onSMSCaptchaError(@SMSCaptchaUtils.ErrorType int event) {
        if (event == SMSCaptchaUtils.SEND_CAPTCHA) {
            Timber.tag(UserScene.SEND_CAPTCHA).w(mResource.getString(event));
            getMvpView().onSentCaptchaCodeFailure(mResource.getString(R.string.send_captcha_error));
        }
    }

    @Override public void onSentCaptchaSuccess(boolean autoReadCaptcha) {
        getMvpView().moveToCaptchaVerify();
    }

    @Override public void onSubmitCaptchaSuccess(String phone, String countryCode) {

    }
}
