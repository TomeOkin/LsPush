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
package com.tomeokin.lspush.biz.base.support;

import android.content.res.Resources;

import com.tomeokin.lspush.R;
import com.tomeokin.lspush.biz.common.UserScene;
import com.tomeokin.lspush.data.model.BaseResponse;

import rx.Subscriber;
import timber.log.Timber;

public class CommonSubscriber<T extends BaseResponse> extends Subscriber<T> {
    private final Resources mResource;
    private final int mActionId;
    private final BaseActionCallback mCallback;

    public CommonSubscriber(Resources resource, int actionId, BaseActionCallback callback) {
        mResource = resource;
        mActionId = actionId;
        mCallback = callback;
    }

    @Override
    public void onCompleted() {
        if (mCallback != null) {
            mCallback.onActionSuccess(mActionId, null);
        }
    }

    @Override
    public void onError(Throwable e) {
        Timber.tag(UserScene.TAG_DATABASE).w(e);
        if (mCallback != null) {
            mCallback.onActionFailure(mActionId, null, mResource.getString(R.string.unexpected_error));
        }
    }

    @Override
    public void onNext(T t) {
        if (mCallback != null) {
            mCallback.onActionSuccess(mActionId, t);
        }
    }
}
