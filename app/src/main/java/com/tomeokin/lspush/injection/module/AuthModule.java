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

import com.google.gson.Gson;
import com.squareup.sqlbrite.BriteDatabase;
import com.tomeokin.lspush.biz.auth.usercase.CheckCaptchaAction;
import com.tomeokin.lspush.biz.auth.usercase.CheckUIDAction;
import com.tomeokin.lspush.biz.auth.usercase.LocalUserInfoAction;
import com.tomeokin.lspush.biz.auth.usercase.RegisterAction;
import com.tomeokin.lspush.biz.auth.usercase.SendCaptchaAction;
import com.tomeokin.lspush.biz.auth.usercase.UploadAvatarAction;
import com.tomeokin.lspush.common.PreferenceUtils;
import com.tomeokin.lspush.data.remote.LsPushService;
import com.tomeokin.lspush.injection.qualifier.ActivityContext;
import com.tomeokin.lspush.injection.scope.PerActivity;

import dagger.Module;
import dagger.Provides;

@Module
public class AuthModule {
    @Provides
    @PerActivity
    public SendCaptchaAction provideSendCaptchaAction(@ActivityContext Context context, LsPushService lsPushService) {
        return new SendCaptchaAction(context.getResources(), lsPushService);
    }

    @Provides
    @PerActivity
    public CheckCaptchaAction provideCheckCaptchaAction(@ActivityContext Context context, LsPushService lsPushService,
        Gson gson) {
        return new CheckCaptchaAction(context.getResources(), lsPushService, gson);
    }

    @Provides
    @PerActivity
    public CheckUIDAction provideCheckUIDAction(@ActivityContext Context context, LsPushService lsPushService) {
        return new CheckUIDAction(context.getResources(), lsPushService);
    }

    @Provides
    @PerActivity
    public UploadAvatarAction provideUploadAvatarAction(@ActivityContext Context context, LsPushService lsPushService) {
        return new UploadAvatarAction(context.getResources(), lsPushService);
    }

    @Provides
    @PerActivity
    public RegisterAction provideRegisterAction(@ActivityContext Context context, LsPushService lsPushService,
        Gson gson) {
        return new RegisterAction(context.getResources(), lsPushService, gson);
    }

    @Provides
    @PerActivity
    public LocalUserInfoAction provideLocalUserInfoAction(@ActivityContext Context context,
        BriteDatabase briteDatabase, PreferenceUtils preferenceUtils) {
        return new LocalUserInfoAction(context.getResources(), briteDatabase, preferenceUtils);
    }
}