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
package com.tomeokin.lspush.biz.usercase.collection;

import android.content.res.Resources;

import com.tomeokin.lspush.biz.base.support.BaseAction;
import com.tomeokin.lspush.biz.base.support.CommonCallback;
import com.tomeokin.lspush.biz.common.UserScene;
import com.tomeokin.lspush.biz.usercase.user.LsPushUserState;
import com.tomeokin.lspush.data.model.BaseResponse;
import com.tomeokin.lspush.data.model.Collection;
import com.tomeokin.lspush.data.model.CollectionResponse;
import com.tomeokin.lspush.data.model.User;
import com.tomeokin.lspush.data.remote.LsPushService;

import retrofit2.Call;

public class CollectionAction extends BaseAction {
    private final LsPushService mLsPushService;
    private final LsPushUserState mLsPushUserState;

    private Call<CollectionResponse> mObtainLatestCollectionsCall;
    private Call<BaseResponse> mPostCollectionCall;

    public CollectionAction(Resources resources, LsPushService lsPushService, LsPushUserState lsPushUserState) {
        super(resources);
        mLsPushService = lsPushService;
        mLsPushUserState = lsPushUserState;
    }

    public void obtainLatestCollection(int page, int size) {
        checkAndCancel(mObtainLatestCollectionsCall);
        mObtainLatestCollectionsCall = mLsPushService.getLatestCollections(mLsPushUserState.getUid(), page, size);
        mObtainLatestCollectionsCall.enqueue(
            new CommonCallback<CollectionResponse>(mResource, UserScene.ACTION_OBTAIN_LATEST_COLLECTIONS, mCallback));
    }

    public void postCollection(Collection collection) {
        User user = new User();
        user.setUid(mLsPushUserState.getUid());
        collection.setUser(user);
        checkAndCancel(mPostCollectionCall);
        mPostCollectionCall = mLsPushService.postCollection(mLsPushUserState.getExpireTokenString(), collection);
        mPostCollectionCall.enqueue(new CommonCallback<>(mResource, UserScene.ACTION_POST_COLLECTION, mCallback));
    }

    @Override
    public void cancel(int action) {
        super.cancel(action);
        if (action == UserScene.ACTION_OBTAIN_LATEST_COLLECTIONS) {
            checkAndCancel(mObtainLatestCollectionsCall);
        } else if (action == UserScene.ACTION_POST_COLLECTION) {
            checkAndCancel(mPostCollectionCall);
        }
    }

    @Override
    public void detach() {
        super.detach();
        checkAndCancel(mObtainLatestCollectionsCall);
        checkAndCancel(mPostCollectionCall);
    }
}
