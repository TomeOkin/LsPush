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

import android.content.res.Resources;

import com.google.gson.Gson;
import com.tomeokin.lspush.R;
import com.tomeokin.lspush.biz.base.support.BaseAction;
import com.tomeokin.lspush.biz.base.support.CommonCallback;
import com.tomeokin.lspush.biz.common.UserScene;
import com.tomeokin.lspush.data.crypt.Crypto;
import com.tomeokin.lspush.data.model.AccessResponse;
import com.tomeokin.lspush.data.model.CryptoToken;
import com.tomeokin.lspush.data.model.LoginData;
import com.tomeokin.lspush.data.remote.LsPushService;

import retrofit2.Call;
import rx.Observable;
import rx.Subscriber;
import rx.functions.Func1;
import timber.log.Timber;

public class LoginAction extends BaseAction {
    private final LsPushService mLsPushService;
    private final Gson mGson;
    private Call<AccessResponse> mLoginCall = null;

    public LoginAction(Resources resources, LsPushService lsPushService, Gson gson) {
        super(resources);
        mLsPushService = lsPushService;
        mGson = gson;
    }

    public void login(LoginData loginData) {
        String data = mGson.toJson(loginData, LoginData.class);
        CryptoToken cryptoToken;
        try {
            cryptoToken = Crypto.get().encrypt(data);
        } catch (Exception e) {
            Timber.w(e);
            mCallback.onActionFailure(UserScene.ACTION_REGISTER, null, mResource.getString(R.string.unexpected_error));
            return;
        }

        checkAndCancel(mLoginCall);
        mLoginCall = mLsPushService.login(cryptoToken);
        mLoginCall.enqueue(new CommonCallback<AccessResponse>(mResource, UserScene.ACTION_LOGIN, mCallback));
    }

    public Observable<AccessResponse> loginObservable(final LoginData loginData) {
        return Observable.create(new Observable.OnSubscribe<CryptoToken>() {
            @Override
            public void call(Subscriber<? super CryptoToken> subscriber) {
                String data = mGson.toJson(loginData, LoginData.class);
                try {
                    CryptoToken cryptoToken = Crypto.get().encrypt(data);
                    subscriber.onNext(cryptoToken);
                } catch (Exception e) {
                    Timber.w(e);
                    subscriber.onError(e);
                }
            }
        }).concatMap(new Func1<CryptoToken, Observable<AccessResponse>>() {
            @Override
            public Observable<AccessResponse> call(CryptoToken cryptoToken) {
                return mLsPushService.loginObservable(cryptoToken);
            }
        });
    }

    @Override
    public void detach() {
        super.detach();
        checkAndCancel(mLoginCall);
        mLoginCall = null;
    }

    @Override
    public void cancel(int action) {
        super.cancel(action);
        if (action == UserScene.ACTION_LOGIN) {
            checkAndCancel(mLoginCall);
        }
    }
}
