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
package com.tomeokin.lspush.biz.base;

import android.content.res.Resources;

import com.tomeokin.lspush.R;
import com.tomeokin.lspush.biz.common.UserScene;
import com.tomeokin.lspush.data.model.BaseResponse;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

public class CommonCallback<T extends BaseResponse> implements Callback<T> {
    private final Resources mResource;
    private final int mActionId;
    private final BaseActionCallback mCallback;

    public CommonCallback(Resources resources, int actionId, BaseActionCallback callback) {
        mResource = resources;
        mActionId = actionId;
        mCallback = callback;
    }

    @Override
    public void onResponse(Call<T> call, Response<T> response) {
        if (response.isSuccessful()) {
            BaseResponse baseResponse = response.body();
            if (baseResponse.getResultCode() == BaseResponse.COMMON_SUCCESS) {
                if (mCallback != null) {
                    mCallback.onActionSuccess(mActionId, baseResponse);
                }
            } else {
                if (mCallback != null) {
                    mCallback.onActionFailure(mActionId, baseResponse, baseResponse.getResult());
                }
            }
        } else {
            try {
                Timber.tag(UserScene.TAG_NETWORK).w(response.errorBody().string());
                if (mCallback != null) {
                    mCallback.onActionFailure(mActionId, null, mResource.getString(R.string.network_abnormal));
                }
            } catch (IOException e) {
                // ignore
            }
        }
    }

    @Override
    public void onFailure(Call<T> call, Throwable t) {
        Timber.w(t);
        if (mCallback != null) {
            mCallback.onActionFailure(mActionId, null, mResource.getString(R.string.unexpected_error));
        }
    }
}
