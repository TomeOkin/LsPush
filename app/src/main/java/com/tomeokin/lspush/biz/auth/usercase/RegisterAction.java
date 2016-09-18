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

import com.google.gson.Gson;
import com.tomeokin.lspush.R;
import com.tomeokin.lspush.biz.base.BaseAction;
import com.tomeokin.lspush.biz.base.BaseActionCallback;
import com.tomeokin.lspush.biz.base.CommonCallback;
import com.tomeokin.lspush.biz.common.UserScene;
import com.tomeokin.lspush.data.crypt.Crypto;
import com.tomeokin.lspush.data.model.AccessResponse;
import com.tomeokin.lspush.data.model.CryptoToken;
import com.tomeokin.lspush.data.model.RegisterData;
import com.tomeokin.lspush.data.remote.LsPushService;

import retrofit2.Call;
import timber.log.Timber;

public class RegisterAction extends BaseAction {
    private final LsPushService mLsPushService;
    private final Gson mGson;
    private Call<AccessResponse> mRegisterCall = null;

    public RegisterAction(Resources resources, LsPushService lsPushService, Gson gson) {
        super(resources);
        mLsPushService = lsPushService;
        mGson = gson;
    }

    public void register(RegisterData registerData) {
        String data = mGson.toJson(registerData, RegisterData.class);
        CryptoToken cryptoToken;
        try {
            cryptoToken = Crypto.encrypt(data);
        } catch (Exception e) {
            Timber.w(e);
            mCallback.onActionFailure(UserScene.ACTION_REGISTER, null, mResource.getString(R.string.unexpected_error));
            return;
        }

        checkAndCancel(mRegisterCall);
        mRegisterCall = mLsPushService.register(cryptoToken);
        mRegisterCall.enqueue(new CommonCallback<AccessResponse>(mResource, UserScene.ACTION_REGISTER, mCallback));
    }

    @Override
    public void attach(BaseActionCallback callback) {
        super.attach(callback);
    }

    @Override
    public void detach() {
        super.detach();
        checkAndCancel(mRegisterCall);
        mRegisterCall = null;
    }

    @Override
    public void cancel(int action) {
        super.cancel(action);
        if (action == UserScene.ACTION_REGISTER) {
            checkAndCancel(mRegisterCall);
        }
    }
}
