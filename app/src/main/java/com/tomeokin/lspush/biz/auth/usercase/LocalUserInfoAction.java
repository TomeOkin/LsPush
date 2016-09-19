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

import android.content.ContentValues;
import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;

import com.squareup.sqlbrite.BriteDatabase;
import com.tomeokin.lspush.biz.base.BaseAction;
import com.tomeokin.lspush.biz.base.CommonSubscriber;
import com.tomeokin.lspush.biz.common.NoGuarantee;
import com.tomeokin.lspush.biz.common.UserScene;
import com.tomeokin.lspush.common.PreferenceUtils;
import com.tomeokin.lspush.data.local.Db;
import com.tomeokin.lspush.data.model.AccessResponse;
import com.tomeokin.lspush.data.model.BaseResponse;
import com.tomeokin.lspush.data.model.User;
import com.tomeokin.lspush.data.model.internal.ListResponse;

import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class LocalUserInfoAction extends BaseAction {
    public static final String ACCESS_RESPONSE = "access.response";
    public static final String LOGIN_USER_PASSWORD = "login.user.password";

    private final BriteDatabase mBriteDatabase;
    private final PreferenceUtils mPreferenceUtils;
    private Subscription mUserLoginSubscription;
    private Subscription mUserLogoutSubscription;
    private Subscription mGetAccessResponseSubscription;
    private Subscription mUpdateAccessResponseSubscription;
    private Subscription mGetHistoryLoginUserSubscription;

    public LocalUserInfoAction(Resources resources, BriteDatabase briteDatabase, PreferenceUtils preferenceUtils) {
        super(resources);
        mBriteDatabase = briteDatabase;
        mPreferenceUtils = preferenceUtils;
    }

    public void userLogin(final AccessResponse accessResponse, final User user) {
        checkAndUnsubscribe(mUserLoginSubscription);
        mUserLoginSubscription = Observable.create(new Observable.OnSubscribe<BaseResponse>() {
            @Override
            public void call(Subscriber<? super BaseResponse> subscriber) {
                try {
                    mPreferenceUtils.put(ACCESS_RESPONSE, accessResponse);
                    mPreferenceUtils.put(LOGIN_USER_PASSWORD, user.getPassword());

                    ContentValues values = Db.UserTable.toContentValues(user);
                    mBriteDatabase.insert(Db.UserTable.TABLE_NAME, values, SQLiteDatabase.CONFLICT_REPLACE);
                    if (!mUserLogoutSubscription.isUnsubscribed()) {
                        subscriber.onNext(null);
                    }
                } catch (Exception e) {
                    if (!mUserLogoutSubscription.isUnsubscribed()) {
                        subscriber.onError(e);
                    }
                }
            }
        })
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(new CommonSubscriber<>(mResource, UserScene.ACTION_DATA_USER_LOGIN, mCallback));
    }

    public void userLogout() {
        checkAndUnsubscribe(mUserLogoutSubscription);
        mUserLogoutSubscription = Observable.create(new Observable.OnSubscribe<BaseResponse>() {
            @Override
            public void call(Subscriber<? super BaseResponse> subscriber) {
                boolean result = mPreferenceUtils.clearAll();
                if (!mUserLogoutSubscription.isUnsubscribed()) {
                    if (result) {
                        subscriber.onNext(null);
                    } else {
                        subscriber.onError(null);
                    }
                }
            }
        })
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(new CommonSubscriber<>(mResource, UserScene.ACTION_DATA_USER_LOGOUT, mCallback));
    }

    public void getAccessResponse() {
        checkAndUnsubscribe(mGetAccessResponseSubscription);
        mGetAccessResponseSubscription = Observable.create(new Observable.OnSubscribe<AccessResponse>() {
            @Override
            public void call(Subscriber<? super AccessResponse> subscriber) {
                AccessResponse response = mPreferenceUtils.get(ACCESS_RESPONSE, AccessResponse.class);
                if (!mGetHistoryLoginUserSubscription.isUnsubscribed()) {
                    subscriber.onNext(response);
                }
            }
        })
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(new CommonSubscriber<>(mResource, UserScene.ACTION_GET_ACCESS_RESPONSE, mCallback));
    }

    public void updateAccessResponse(final AccessResponse accessResponse) {
        checkAndUnsubscribe(mUpdateAccessResponseSubscription);
        mUpdateAccessResponseSubscription = Observable.create(new Observable.OnSubscribe<BaseResponse>() {
            @Override
            public void call(Subscriber<? super BaseResponse> subscriber) {
                try {
                    mPreferenceUtils.put(ACCESS_RESPONSE, accessResponse);
                    if (!mUpdateAccessResponseSubscription.isUnsubscribed()) {
                        subscriber.onNext(null);
                    }
                } catch (Exception e) {
                    subscriber.onError(e);
                }
            }
        })
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(new CommonSubscriber<>(mResource, UserScene.ACTION_UPDATE_ACCESS_RESPONSE, mCallback));
    }

    public void getHistoryLoginUser() {
        checkAndUnsubscribe(mGetHistoryLoginUserSubscription);
        mGetHistoryLoginUserSubscription = mBriteDatabase.createQuery(Db.UserTable.TABLE_NAME, Db.UserTable.QUERY_ALL)
            .mapToList(Db.UserTable.MAPPER)
            .flatMap(new Func1<List<User>, Observable<ListResponse<User>>>() {
                @Override
                public Observable<ListResponse<User>> call(List<User> users) {
                    return Observable.just(new ListResponse<>(users));
                }
            })
            .observeOn(Schedulers.io())
            .subscribeOn(AndroidSchedulers.mainThread())
            .subscribe(new CommonSubscriber<ListResponse<User>>(mResource, UserScene.ACTION_GET_HISTORY_LOGIN_USER,
                mCallback));
    }

    @Override
    public void detach() {
        super.detach();
        checkAndUnsubscribe(mUserLoginSubscription);
        mUserLoginSubscription = null;
        checkAndUnsubscribe(mUserLogoutSubscription);
        mUserLogoutSubscription = null;
        checkAndUnsubscribe(mGetAccessResponseSubscription);
        mGetAccessResponseSubscription = null;
        checkAndUnsubscribe(mUpdateAccessResponseSubscription);
        mUpdateAccessResponseSubscription = null;
        checkAndUnsubscribe(mGetHistoryLoginUserSubscription);
        mGetHistoryLoginUserSubscription = null;
    }

    @NoGuarantee
    @Override
    public void cancel(int action) {
        super.cancel(action);
        if (action == UserScene.ACTION_DATA_USER_LOGIN) {
            checkAndUnsubscribe(mUserLoginSubscription);
        } else if (action == UserScene.ACTION_DATA_USER_LOGOUT) {
            checkAndUnsubscribe(mUserLogoutSubscription);
        } else if (action == UserScene.ACTION_GET_ACCESS_RESPONSE) {
            checkAndUnsubscribe(mGetAccessResponseSubscription);
        } else if (action == UserScene.ACTION_UPDATE_ACCESS_RESPONSE) {
            checkAndUnsubscribe(mUpdateAccessResponseSubscription);
        } else if (action == UserScene.ACTION_GET_HISTORY_LOGIN_USER) {
            checkAndUnsubscribe(mGetHistoryLoginUserSubscription);
        }
    }
}
