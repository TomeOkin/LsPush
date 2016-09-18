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

import android.content.res.Resources;

import com.tomeokin.lspush.biz.base.BaseAction;
import com.tomeokin.lspush.data.local.UserManager;
import com.tomeokin.lspush.data.model.AccessResponse;
import com.tomeokin.lspush.data.model.User;

public class UpdateLocalUserInfoAction extends BaseAction {
    private final UserManager mUserManager;

    public UpdateLocalUserInfoAction(Resources resources, UserManager userManager) {
        super(resources);
        mUserManager = userManager;
    }

    public void updateUserInfo(AccessResponse accessResponse, User user) {
        mUserManager.login(user);
        mUserManager.putExpireTime(accessResponse.getExpireTime());
        mUserManager.putRefreshTime(accessResponse.getRefreshTime());
        mUserManager.putExpireToken(accessResponse.getExpireToken());
        mUserManager.putRefreshToken(accessResponse.getRefreshToken());
    }
}
