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
import com.tomeokin.lspush.biz.base.CommonCallback;
import com.tomeokin.lspush.biz.common.UserScene;
import com.tomeokin.lspush.data.crypt.Crypto;
import com.tomeokin.lspush.data.model.AccessResponse;
import com.tomeokin.lspush.data.model.BaseResponse;
import com.tomeokin.lspush.data.model.CryptoToken;
import com.tomeokin.lspush.data.model.RegisterData;
import com.tomeokin.lspush.data.remote.LsPushService;
import com.tomeokin.lspush.injection.qualifier.ActivityContext;
import com.tomeokin.lspush.injection.scope.PerActivity;

import javax.inject.Inject;

import retrofit2.Call;
import timber.log.Timber;

@PerActivity
public class RegisterPresenter extends BasePresenter<RegisterView> {
    private final LsPushService mLsPushService;
    private final Resources mResource;
    private final Gson mGson;
    private Call<BaseResponse> mCheckUIDCall;
    private Call<AccessResponse> mRegisterCall;

    @Inject public RegisterPresenter(LsPushService lsPushService, @ActivityContext Context context, Gson gson) {
        mLsPushService = lsPushService;
        mResource = context.getResources();
        mGson = gson;
    }

    public void checkUIDExist(String uid) {
        checkAndCancel(mCheckUIDCall);
        mCheckUIDCall = mLsPushService.checkUIDExisted(uid);
        mCheckUIDCall.enqueue(new CommonCallback<>(mResource, UserScene.ACTION_CHECK_UID, getMvpView()));
    }

    public void register(RegisterData registerData) {
        String data = mGson.toJson(registerData, RegisterData.class);
        Timber.i("register-data %s", data);
        CryptoToken cryptoToken;
        try {
            cryptoToken = Crypto.encrypt(data);
        } catch (Exception e) {
            Timber.w(e);
            getMvpView().onActionFailure(UserScene.ACTION_REGISTER, null, mResource.getString(R.string.unexpected_error));
            return;
        }

        checkAndCancel(mRegisterCall);
        mRegisterCall = mLsPushService.register(cryptoToken);
        mRegisterCall.enqueue(new CommonCallback<AccessResponse>(mResource, UserScene.ACTION_REGISTER, getMvpView()));
    }

    public void cancel(int action) {
        if (action == UserScene.ACTION_CHECK_UID) {
            checkAndCancel(mCheckUIDCall);
        } else if (action == UserScene.ACTION_REGISTER) {
            checkAndCancel(mRegisterCall);
        }
    }

    @Override
    public void detachView() {
        super.detachView();
        checkAndCancel(mCheckUIDCall);
        checkAndCancel(mRegisterCall);
        mCheckUIDCall = null;
        mRegisterCall = null;
    }
}
