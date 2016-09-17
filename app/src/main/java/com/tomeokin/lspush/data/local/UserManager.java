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
package com.tomeokin.lspush.data.local;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import com.facebook.android.crypto.keychain.AndroidConceal;
import com.google.gson.Gson;
import com.squareup.sqlbrite.BriteDatabase;
import com.tomeokin.lspush.common.PreferenceUtils;
import com.tomeokin.lspush.data.crypt.BeeConcealKeyChain;
import com.tomeokin.lspush.data.model.CryptoToken;
import com.tomeokin.lspush.data.model.User;
import com.tomeokin.lspush.injection.qualifier.AppContext;

import java.util.List;

import rx.Observable;
import rx.functions.Func1;

/**
 * Before hawk release or provide multiple hawk instance, use normal way to store the data.
 */
public class UserManager {
    public static final String USER_PREFERENCE = "user.preferences";

    public static final String HISTORY_LOGIN_USER = "history.login.user";
    public static final String LOGIN_USER_UID = "login.user.uid";
    public static final String LOGIN_USER_PASSWORD = "login.user.password";
    public static final String EXPIRE_TOKEN = "expire.token";
    public static final String REFRESH_TOKEN = "refresh.token";
    public static final String EXPIRE_TIME = "expire.time";
    public static final String REFRESH_TIME = "refresh.time";

    private final PreferenceUtils mUtils;
    private final BriteDatabase mDb;

    private static UserManager sInstance;
    private static User mUser = null;
    private static CryptoToken mExpireToken = null;
    private static CryptoToken mRefreshToken = null;
    private static long mExpireTime = 0;
    private static long mRefreshTime = 0;

    public static UserManager get(@AppContext Context context, Gson gson, BriteDatabase db) {
        if (sInstance == null) {
            sInstance = new UserManager(context, gson, db);
        }
        return sInstance;
    }

    public UserManager(@AppContext Context context, Gson gson, BriteDatabase db) {
        SharedPreferences preferences = context.getSharedPreferences(USER_PREFERENCE, Context.MODE_PRIVATE);
        BeeConcealKeyChain keyChain = new BeeConcealKeyChain(context);
        com.facebook.crypto.Crypto crypto = AndroidConceal.get().createCrypto256Bits(keyChain);
        mUtils = new PreferenceUtils(gson, preferences, crypto);
        mDb = db;
    }

    private boolean putHistoryLoginUser(final User user) {
        try {
            // insert or replace may change your Id, but in this, I don't use a auto-increasing id
            // for another, see http://www.trinea.cn/android/sqlite-insert-or-update/
            ContentValues values = Db.UserTable.toContentValues(user);
            mDb.insert(Db.UserTable.TABLE_NAME, values, SQLiteDatabase.CONFLICT_REPLACE);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public Observable<List<User>> getHistoryLoginUser() {
        return mDb.createQuery(Db.UserTable.TABLE_NAME, Db.UserTable.QUERY_ALL).mapToList(new Func1<Cursor, User>() {
            @Override
            public User call(Cursor cursor) {
                return Db.UserTable.parseCursor(cursor);
            }
        });
    }

    public boolean hasHistoryUser() {
        return mUtils.get(HISTORY_LOGIN_USER, Boolean.class);
    }

    public boolean hasUserLogin() {
        return mUser != null || !TextUtils.isEmpty((CharSequence) mUtils.get(LOGIN_USER_UID, String.class));
    }

    public boolean login(User user) {
        mUser = user;
        boolean result = mUtils.put(LOGIN_USER_UID, mUser.getUid())
            && mUtils.put(LOGIN_USER_PASSWORD, user.getPassword())
            && putHistoryLoginUser(user);
        mUtils.put(HISTORY_LOGIN_USER, true);
        mUser.setPassword(null);
        return result;
    }

    public boolean logout() {
        boolean result = mUtils.clearAll();
        mUtils.put(HISTORY_LOGIN_USER, true);
        return result;
    }

    public boolean putExpireTime(long expireTime) {
        mExpireTime = expireTime;
        return mUtils.put(EXPIRE_TIME, expireTime);
    }

    public long getExpireTime() {
        if (mExpireTime <= 0) {
            mExpireTime = mUtils.get(EXPIRE_TIME, Long.class);
        }
        return mExpireTime;
    }

    public boolean putRefreshTime(long refreshTime) {
        mRefreshTime = refreshTime;
        return mUtils.put(REFRESH_TIME, refreshTime);
    }

    public long getRefreshTime() {
        if (mRefreshTime <= 0) {
            mRefreshTime = mUtils.get(REFRESH_TIME, Long.class);
        }
        return mRefreshTime;
    }

    public boolean putExpireToken(CryptoToken expireToken) {
        mExpireToken = expireToken;
        return mUtils.put(EXPIRE_TOKEN, expireToken);
    }

    public CryptoToken getExpireToken() {
        if (mExpireToken == null) {
            mExpireToken = mUtils.get(EXPIRE_TOKEN, CryptoToken.class);
        }
        return mExpireToken;
    }

    public boolean putRefreshToken(CryptoToken refreshToken) {
        mRefreshToken = refreshToken;
        return mUtils.put(REFRESH_TOKEN, refreshToken);
    }

    public CryptoToken getRefreshToken() {
        if (mRefreshToken == null) {
            mRefreshToken = mUtils.get(REFRESH_TOKEN, CryptoToken.class);
        }
        return mRefreshToken;
    }
}
