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
package com.tomeokin.lspush.biz.usercase.user;

import android.support.annotation.Nullable;

import com.tomeokin.lspush.data.model.AccessResponse;

/**
 * Compare to Action, it will store app state, that mean it have data, but it is not action support.
 */
public class LsPushUserState {
    private AccessResponse mAccessResponse;

    public LsPushUserState() {
    }

    public AccessResponse getAccessResponse() {
        return mAccessResponse;
    }

    public synchronized void setAccessResponse(@Nullable AccessResponse accessResponse) {
        mAccessResponse = accessResponse;
    }

    //public static boolean checkNeedToRefreshExpireToken(@NonNull AccessResponse accessResponse) {
    //    long now
    //    if (accessResponse.getExpireTime() >= new Date().getTime()) {
    //
    //    }
    //}
    //
    //public static boolean
}