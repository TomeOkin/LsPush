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
package com.tomeokin.lspush.biz.auth.usercase;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.tomeokin.lspush.R;
import com.tomeokin.lspush.biz.base.BaseAction;
import com.tomeokin.lspush.biz.base.BaseActionCallback;
import com.tomeokin.lspush.biz.base.CommonCallback;
import com.tomeokin.lspush.biz.common.NoGuarantee;
import com.tomeokin.lspush.biz.common.UserScene;
import com.tomeokin.lspush.common.SMSCaptchaUtils;
import com.tomeokin.lspush.data.model.BaseResponse;
import com.tomeokin.lspush.data.model.CaptchaRequest;
import com.tomeokin.lspush.data.remote.LsPushService;

import cn.smssdk.EventHandler;
import retrofit2.Call;
import timber.log.Timber;

public class SendCaptchaAction extends BaseAction implements BaseActionCallback {
    private final LsPushService mLsPushService;
    private SMSCaptchaUtils.SMSHandler mHandler;
    private EventHandler mEventHandler;
    private Call<BaseResponse> mSendCaptchaCall;

    public SendCaptchaAction(BaseActionCallback callback, Resources resources, LsPushService lsPushService) {
        super(callback, resources);
        mLsPushService = lsPushService;
    }

    public void sendCaptchaCode(CaptchaRequest request, String countryCode) {
        if (request.getSendObject().contains("@")) {
            checkAndCancel(mSendCaptchaCall);
            mSendCaptchaCall = mLsPushService.sendCaptcha(request);
            mSendCaptchaCall.enqueue(new CommonCallback<>(mResource, UserScene.ACTION_SEND_CAPTCHA, mCallback));
        } else {
            SMSCaptchaUtils.sendCaptcha(countryCode, request.getSendObject());
        }
    }

    @Override
    public void onActionFailure(int action, @Nullable BaseResponse response, String message) {
        if (action == SMSCaptchaUtils.SEND_CAPTCHA) {
            Timber.tag(UserScene.SEND_CAPTCHA).w(mResource.getString(action));
            mCallback.onActionFailure(UserScene.ACTION_SEND_CAPTCHA, response,
                mResource.getString(R.string.send_captcha_error));
        }
    }

    @Override
    public void onActionSuccess(int action, @Nullable BaseResponse response) {
        if (action == SMSCaptchaUtils.SEND_CAPTCHA) {
            mCallback.onActionSuccess(UserScene.ACTION_SEND_CAPTCHA, response);
        }
    }

    @Override
    public void onViewCreate(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreate(view, savedInstanceState);
        mHandler = new SMSCaptchaUtils.SMSHandler(this);
        mEventHandler = new SMSCaptchaUtils.CustomEventHandler(mHandler);
        SMSCaptchaUtils.registerEventHandler(mEventHandler);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mHandler != null) {
            mHandler.removeAllMessage();
        }
        mHandler = null;
        SMSCaptchaUtils.unregisterEventHandler(mEventHandler);
        mEventHandler = null;
        checkAndCancel(mSendCaptchaCall);
        mSendCaptchaCall = null;
    }

    @NoGuarantee
    @Override
    public void cancel(int action) {
        super.cancel(action);
        if (action == UserScene.ACTION_SEND_CAPTCHA) {
            checkAndCancel(mSendCaptchaCall);
        }
    }
}
