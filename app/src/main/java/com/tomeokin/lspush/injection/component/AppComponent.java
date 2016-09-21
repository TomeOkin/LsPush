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
package com.tomeokin.lspush.injection.component;

import android.app.Application;
import android.content.Context;

import com.google.gson.Gson;
import com.squareup.sqlbrite.BriteDatabase;
import com.tomeokin.lspush.biz.usercase.user.LsPushUserState;
import com.tomeokin.lspush.biz.usercase.LocalUserInfoAction;
import com.tomeokin.lspush.common.PreferenceUtils;
import com.tomeokin.lspush.data.remote.LsPushService;
import com.tomeokin.lspush.injection.module.AppModule;
import com.tomeokin.lspush.injection.module.DataModule;
import com.tomeokin.lspush.injection.module.LsPushApiModule;
import com.tomeokin.lspush.injection.module.UserModule;
import com.tomeokin.lspush.injection.qualifier.AppContext;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {AppModule.class, LsPushApiModule.class, DataModule.class, UserModule.class})
public interface AppComponent {
    @AppContext
    Context context();

    Application application();

    LsPushService lspushService();

    Gson gson();

    BriteDatabase briteDatabase();

    PreferenceUtils preferenceUtils();

    LsPushUserState lspushUserState();

    LocalUserInfoAction localUserInfoAction();
}
