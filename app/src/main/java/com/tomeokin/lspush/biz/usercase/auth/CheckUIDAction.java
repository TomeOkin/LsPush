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

import com.tomeokin.lspush.biz.base.support.BaseAction;
import com.tomeokin.lspush.biz.base.support.CommonCallback;
import com.tomeokin.lspush.biz.common.UserScene;
import com.tomeokin.lspush.data.model.BaseResponse;
import com.tomeokin.lspush.data.remote.LsPushService;

import retrofit2.Call;

public class CheckUIDAction extends BaseAction {
    private final LsPushService mLsPushService;
    private Call<BaseResponse> mCheckUIDCall = null;

    public CheckUIDAction(Resources resources, LsPushService lsPushService) {
        super(resources);
        mLsPushService = lsPushService;
    }

    public void checkUIDExist(String uid) {
        checkAndCancel(mCheckUIDCall);
        mCheckUIDCall = mLsPushService.checkUIDExisted(uid);
        mCheckUIDCall.enqueue(new CommonCallback<>(mResource, UserScene.ACTION_CHECK_UID, mCallback));
    }

    @Override
    public void detach() {
        super.detach();
        checkAndCancel(mCheckUIDCall);
        mCheckUIDCall = null;
    }

    @Override
    public void cancel(int action) {
        super.cancel(action);
        if (action == UserScene.ACTION_CHECK_UID) {
            checkAndCancel(mCheckUIDCall);
        }
    }
}
