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
package com.tomeokin.lspush.biz.usercase.sync;

import com.google.gson.Gson;
import com.tomeokin.lspush.data.crypt.Crypto;
import com.tomeokin.lspush.data.model.AccessResponse;
import com.tomeokin.lspush.data.model.CryptoToken;
import com.tomeokin.lspush.data.model.RefreshData;
import com.tomeokin.lspush.data.model.RegisterData;
import com.tomeokin.lspush.data.remote.LsPushService;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Func1;
import timber.log.Timber;

public class RefreshTokenAction {
    private final LsPushService mLsPushService;
    private final Gson mGson;

    public RefreshTokenAction(LsPushService lsPushService, Gson gson) {
        mLsPushService = lsPushService;
        mGson = gson;
    }

    public Observable<AccessResponse> refreshExpireToken(CryptoToken refreshToken) {
        return mLsPushService.refreshExpireToken(refreshToken);
    }

    public Observable<AccessResponse> refreshRefreshToken(final AccessResponse accessResponse) {
        return Observable.create(new Observable.OnSubscribe<CryptoToken>() {
            @Override
            public void call(Subscriber<? super CryptoToken> subscriber) {
                RefreshData refreshData = new RefreshData();
                refreshData.setRefreshToken(accessResponse.getRefreshToken());
                refreshData.setUserId(accessResponse.getUser().getUid());
                String data = mGson.toJson(refreshData, RegisterData.class);
                try {
                    CryptoToken cryptoToken = Crypto.get().encrypt(data);
                    subscriber.onNext(cryptoToken);
                } catch (Exception e) {
                    Timber.w(e);
                    subscriber.onError(e);
                }
            }
        }).concatMap(new Func1<CryptoToken, Observable<? extends AccessResponse>>() {
            @Override
            public Observable<? extends AccessResponse> call(CryptoToken cryptoToken) {
                return mLsPushService.refreshRefreshToken(cryptoToken);
            }
        });
    }
}
