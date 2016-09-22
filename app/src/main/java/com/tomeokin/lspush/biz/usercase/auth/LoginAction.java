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
import com.tomeokin.lspush.biz.base.BaseAction;
import com.tomeokin.lspush.data.model.AccessResponse;
import com.tomeokin.lspush.data.remote.LsPushService;

import retrofit2.Call;

public class LoginAction extends BaseAction {
    private final LsPushService mLsPushService;
    private final Gson mGson;
    private Call<AccessResponse> mLoginCall = null;

    public LoginAction(Resources resources, LsPushService lsPushService, Gson gson) {
        super(resources);
        mLsPushService = lsPushService;
        mGson = gson;
    }


}
