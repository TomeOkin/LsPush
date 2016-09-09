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

import com.tomeokin.lspush.R;
import com.tomeokin.lspush.biz.base.BaseActionCallback;
import com.tomeokin.lspush.biz.base.BasePresenter;
import com.tomeokin.lspush.biz.base.CommonCallback;
import com.tomeokin.lspush.biz.common.UserScene;
import com.tomeokin.lspush.common.SMSCaptchaUtils;
import com.tomeokin.lspush.data.model.BaseResponse;
import com.tomeokin.lspush.data.model.CaptchaRequest;
import com.tomeokin.lspush.data.remote.LsPushService;
import com.tomeokin.lspush.injection.qualifier.ActivityContext;
import com.tomeokin.lspush.injection.scope.PerActivity;

import java.util.List;

import javax.inject.Inject;

import retrofit2.Call;
import timber.log.Timber;

@PerActivity
public class CaptchaPresenter extends BasePresenter<CaptchaView> implements BaseActionCallback {
    private final LsPushService mLsPushService;
    private final Resources mResource;
    private int mSendCaptchaRequest;

    @Inject public CaptchaPresenter(LsPushService lsPushService, @ActivityContext Context context) {
        mLsPushService = lsPushService;
        mResource = context.getResources();
    }

    public List<String> getHistoryUserEmails() {
        return null;
    }

    public void sendCaptchaCode(int action, CaptchaRequest request, String countryCode) {
        mSendCaptchaRequest = action;
        if (request.getSendObject().contains("@")) {
            Call<BaseResponse> call = mLsPushService.sendCaptcha(request);
            call.enqueue(new CommonCallback<>(mResource, action, getMvpView()));
        } else {
            SMSCaptchaUtils.sendCaptcha(countryCode, request.getSendObject());
        }
    }

    @Override
    public void onActionFailure(int action, String message) {
        if (action == SMSCaptchaUtils.SEND_CAPTCHA) {
            Timber.tag(UserScene.SEND_CAPTCHA).w(mResource.getString(action));
            getMvpView().onActionFailure(mSendCaptchaRequest, mResource.getString(R.string.send_captcha_error));
        }
    }

    @Override
    public void onActionSuccess(int action, @Nullable BaseResponse response) {
        if (action == SMSCaptchaUtils.SEND_CAPTCHA) {
            getMvpView().onActionSuccess(mSendCaptchaRequest, response);
        }
    }
}
