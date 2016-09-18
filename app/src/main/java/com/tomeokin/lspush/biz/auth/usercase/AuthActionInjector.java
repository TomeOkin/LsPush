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

import android.content.Context;
import android.content.res.Resources;

import com.google.gson.Gson;
import com.tomeokin.lspush.biz.base.BaseActionCallback;
import com.tomeokin.lspush.data.remote.LsPushService;
import com.tomeokin.lspush.injection.qualifier.ActivityContext;
import com.tomeokin.lspush.injection.scope.PerActivity;

import javax.inject.Inject;

@PerActivity
public class AuthActionInjector {
    private final LsPushService mLsPushService;
    private final Context mContext;
    private final Resources mResource;
    private final Gson mGson;

    @Inject
    public AuthActionInjector(LsPushService lsPushService, @ActivityContext Context context, Gson gson) {
        mLsPushService = lsPushService;
        mContext = context;
        mResource = context.getResources();
        mGson = gson;
    }

    public SendCaptchaAction getSendCaptchaAction(BaseActionCallback callback) {
        return new SendCaptchaAction(callback, mResource, mLsPushService);
    }

    public CheckCaptchaAction getCheckCaptchaAction(BaseActionCallback callback) {
        return new CheckCaptchaAction(callback, mResource, mLsPushService, mGson);
    }
}
