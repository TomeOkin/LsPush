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
package com.tomeokin.lspush.biz.usercase;

import android.content.res.Resources;

import com.tomeokin.lspush.biz.base.BaseAction;
import com.tomeokin.lspush.biz.base.CommonCallback;
import com.tomeokin.lspush.biz.common.UserScene;
import com.tomeokin.lspush.data.model.UploadResponse;
import com.tomeokin.lspush.data.remote.LsPushService;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;

public class UploadAvatarAction extends BaseAction {
    private final LsPushService mLsPushService;
    private Call<UploadResponse> mUploadCall = null;

    public UploadAvatarAction(Resources resources, LsPushService lsPushService) {
        super(resources);
        mLsPushService = lsPushService;
    }

    public void upload(File file) {
        String descriptionString = "upload user avatar";
        RequestBody description = RequestBody.create(MediaType.parse("multipart/form-data"), descriptionString);

        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
        MultipartBody.Part body = MultipartBody.Part.createFormData("file", file.getName(), requestFile);

        checkAndCancel(mUploadCall);
        mUploadCall = mLsPushService.upload(1, description, body);
        mUploadCall.enqueue(new CommonCallback<UploadResponse>(mResource, UserScene.ACTION_UPLOAD, mCallback));
    }

    @Override
    public void cancel(int action) {
        super.cancel(action);
        if (action == UserScene.ACTION_UPLOAD) {
            checkAndCancel(mUploadCall);
        }
    }

    @Override
    public void detach() {
        super.detach();
        checkAndCancel(mUploadCall);
        mUploadCall = null;
    }
}
