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
package com.tomeokin.lspush.injection.module;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteOpenHelper;

import com.facebook.android.crypto.keychain.AndroidConceal;
import com.google.gson.Gson;
import com.squareup.sqlbrite.BriteDatabase;
import com.squareup.sqlbrite.SqlBrite;
import com.tomeokin.lspush.biz.common.UserScene;
import com.tomeokin.lspush.common.PreferenceUtils;
import com.tomeokin.lspush.data.crypt.BeeConcealKeyChain;
import com.tomeokin.lspush.data.local.DbOpenHelper;
import com.tomeokin.lspush.injection.qualifier.AppContext;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import rx.schedulers.Schedulers;
import timber.log.Timber;

@Module
public class DataModule {
    private static final String USER_PREFERENCE = "lspush_user";

    @Provides
    @Singleton
    SQLiteOpenHelper provideDbOpenHelper(@AppContext Context context) {
        return new DbOpenHelper(context);
    }

    @Provides
    @Singleton
    SqlBrite provideSqlBrite() {
        return SqlBrite.create(new SqlBrite.Logger() {
            @Override
            public void log(String message) {
                Timber.tag(UserScene.TAG_DATABASE).v(message);
            }
        });
    }

    @Provides
    @Singleton
    BriteDatabase provideBriteDatabase(SqlBrite sqlBrite, SQLiteOpenHelper helper) {
        BriteDatabase db = sqlBrite.wrapDatabaseHelper(helper, Schedulers.io());
        db.setLoggingEnabled(true);
        return db;
    }

    @Provides @Singleton
    SharedPreferences provideSharedPreferences(@AppContext Context context) {
        return context.getSharedPreferences(USER_PREFERENCE, Context.MODE_PRIVATE);
    }

    @Provides @Singleton
    com.facebook.crypto.Crypto provideCrypto(@AppContext Context context) {
        BeeConcealKeyChain keyChain = new BeeConcealKeyChain(context);
        return AndroidConceal.get().createCrypto256Bits(keyChain);
    }

    @Provides @Singleton
    PreferenceUtils providePreferenceUtils(Gson gson, SharedPreferences prefs, com.facebook.crypto.Crypto crypto) {
        return new PreferenceUtils(gson, prefs, crypto);
    }
}
